package com.lolc.api.getway.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lolc.api.getway.dto.AccountDTO;
import com.lolc.api.getway.dto.response.KhqrResponse;
import com.lolc.api.getway.entity.Account;
import com.lolc.api.getway.enums.Currency;
import com.lolc.api.getway.exception.ResourceNotFoundException;
import com.lolc.api.getway.mapper.AccountMapper;
import com.lolc.api.getway.repository.AccountRepository;
import com.lolc.api.getway.service.AccountService;
import jakarta.transaction.Transactional;
import kh.gov.nbc.bakong_khqr.BakongKHQR;
import kh.gov.nbc.bakong_khqr.model.CRCValidation;
import kh.gov.nbc.bakong_khqr.model.Constant;
import kh.gov.nbc.bakong_khqr.model.IndividualInfo;
import kh.gov.nbc.bakong_khqr.model.KHQRCurrency;
import kh.gov.nbc.bakong_khqr.model.KHQRData;
import kh.gov.nbc.bakong_khqr.model.KHQRResponse;
import kh.gov.nbc.bakong_khqr.model.KHQRStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private static final String DEFAULT_CITY = "PHNOM PENH";
    private static final String DEFAULT_MERCHANT_CATEGORY_CODE = "5999";
    private static final int QR_SIZE = 300;
    private static final long DYNAMIC_QR_EXPIRE_MINUTES = 15L;
    private static final int MAX_BAKONG_ID_LENGTH = 32;
    private static final int MAX_ACCOUNT_INFORMATION_LENGTH = 32;

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    public Account create(Account account) {
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public List<AccountDTO> findAll() {
        return accountRepository.findAll().stream()
                .map(accountMapper::toAccountDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<AccountDTO> findByCustomerId(Long customerId) {
        return accountRepository.findByCustomer_CustomerId(customerId).stream()
                .map(accountMapper::toAccountDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Account findById(Long accountId) {
        return accountRepository.findById(accountId).orElseThrow(() ->
                new ResourceNotFoundException("Account not found with id " + accountId));
    }

    @Override
    public KhqrResponse generateKhqr(Long accountId, BigDecimal amount, String bakongAccountId) {
        Account account = findById(accountId);
        Currency currency = account.getCurrency() == null ? Currency.USD : account.getCurrency();
        BigDecimal normalizedAmount = normalizeAmount(amount, currency);
        String resolvedBakongAccountId = resolveBakongAccountId(account, bakongAccountId);
        String payload = generateKhqrPayload(account, currency, normalizedAmount, resolvedBakongAccountId);
        String qrCodeBase64 = generateQrCodeBase64(payload);

        return new KhqrResponse(
                account.getAccountId(),
                account.getAccountNumber(),
                resolvedBakongAccountId,
                normalizedAmount,
                payload,
                qrCodeBase64
        );
    }

    @Override
    public Account update(Long accountId, AccountDTO accountDTO) {
        Account account = findById(accountId);
        accountMapper.updateAccountFromDto(accountDTO, account);
        return accountRepository.save(account);
    }

    @Override
    public void delete(Long accountId) {
        Account account = findById(accountId);
        accountRepository.delete(account);
    }

    private BigDecimal normalizeAmount(BigDecimal amount, Currency currency) {
        if (amount == null) {
            return null;
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        BigDecimal normalized = amount.stripTrailingZeros();
        if (currency == Currency.KHR && normalized.scale() > 0) {
            throw new IllegalArgumentException("KHR amount must not contain decimal places");
        }
        if (currency == Currency.USD && normalized.scale() > 2) {
            throw new IllegalArgumentException("USD amount supports up to 2 decimal places");
        }

        return normalized;
    }

    private String resolveBakongAccountId(Account account, String bakongAccountId) {
        String candidate = StringUtils.hasText(bakongAccountId)
                ? bakongAccountId.trim()
                : account.getAccountNumber() == null ? null : account.getAccountNumber().trim();

        if (!StringUtils.hasText(candidate)) {
            throw new IllegalArgumentException("Bakong account ID is required (example: 012345678@bank)");
        }

        if (candidate.length() > MAX_BAKONG_ID_LENGTH) {
            throw new IllegalArgumentException("Bakong account ID must not exceed 32 characters");
        }

        if (!candidate.matches("^[^@]+@[^@]+$")) {
            throw new IllegalArgumentException("Bakong account ID must be in the format name@bank");
        }

        return candidate.toLowerCase(Locale.ROOT);
    }

    private String generateKhqrPayload(Account account, Currency currency, BigDecimal amount, String bakongAccountId) {
        IndividualInfo info = new IndividualInfo();
        info.setBakongAccountId(bakongAccountId);
        info.setCurrency(resolveKhqrCurrency(currency));
        info.setAmount(amount == null ? null : amount.doubleValue());
        info.setMerchantName(resolveMerchantName(account));
        info.setMerchantCity(resolveMerchantCity());
        info.setMerchantCategoryCode(DEFAULT_MERCHANT_CATEGORY_CODE);
        info.setBillNumber("ACC-" + account.getAccountId());

        String accountInformation = sanitizeAccountInformation(account.getAccountNumber());
        if (StringUtils.hasText(accountInformation)) {
            info.setAccountInformation(accountInformation);
        }

        if (amount != null) {
            long expiration = Instant.now().plus(DYNAMIC_QR_EXPIRE_MINUTES, ChronoUnit.MINUTES).toEpochMilli();
            info.setExpirationTimestamp(expiration);
        }

        KHQRResponse<KHQRData> response = BakongKHQR.generateIndividual(info);
        KHQRStatus status = response.getKHQRStatus();
        if (status != null && status.getCode() == Constant.ERROR_CODE) {
            throw new IllegalArgumentException(resolveKhqrErrorMessage(status, "Failed to generate KHQR"));
        }

        KHQRData data = response.getData();
        if (data == null || !StringUtils.hasText(data.getQr())) {
            throw new IllegalStateException("Failed to generate KHQR payload");
        }

        String payload = data.getQr();
        KHQRResponse<CRCValidation> verify = BakongKHQR.verify(payload);
        KHQRStatus verifyStatus = verify.getKHQRStatus();
        CRCValidation crcValidation = verify.getData();
        if ((verifyStatus != null && verifyStatus.getCode() == Constant.ERROR_CODE)
                || crcValidation == null
                || !crcValidation.isValid()) {
            throw new IllegalStateException(resolveKhqrErrorMessage(verifyStatus, "Generated KHQR failed validation"));
        }

        return payload;
    }

    private String resolveKhqrErrorMessage(KHQRStatus status, String fallback) {
        if (status == null) {
            return fallback;
        }

        String message = StringUtils.hasText(status.getMessage()) ? status.getMessage() : fallback;
        Integer errorCode = status.getErrorCode();
        return errorCode == null ? message : message + " (error code: " + errorCode + ")";
    }

    private KHQRCurrency resolveKhqrCurrency(Currency currency) {
        return currency == Currency.KHR ? KHQRCurrency.KHR : KHQRCurrency.USD;
    }

    private String sanitizeAccountInformation(String accountNumber) {
        if (!StringUtils.hasText(accountNumber)) {
            return null;
        }

        String normalized = accountNumber.replaceAll("\\s+", "");
        if (!StringUtils.hasText(normalized) || normalized.contains("@")) {
            return null;
        }

        if (normalized.length() > MAX_ACCOUNT_INFORMATION_LENGTH) {
            return null;
        }

        return normalized;
    }

    private String resolveMerchantName(Account account) {
        String merchantName = "ACCOUNT " + account.getAccountId();
        return sanitizeKhqrText(merchantName, 25, "ACCOUNT");
    }

    private String resolveMerchantCity() {
        return sanitizeKhqrText(DEFAULT_CITY, 15, DEFAULT_CITY);
    }

    private String sanitizeKhqrText(String value, int maxLength, String fallback) {
        String source = StringUtils.hasText(value) ? value : fallback;
        String normalized = Normalizer.normalize(source, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        if (!StringUtils.hasText(normalized)) {
            normalized = fallback;
        }

        if (normalized.length() > maxLength) {
            return normalized.substring(0, maxLength);
        }

        return normalized;
    }

    private String generateQrCodeBase64(String payload) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(payload, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE);
            BufferedImage image = toBufferedImage(matrix);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", outputStream);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (WriterException | IOException ex) {
            throw new IllegalStateException("Failed to generate KHQR image", ex);
        }
    }

    private BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }

        return image;
    }
}
