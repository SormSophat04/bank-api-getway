package com.lolc.api.getway.impl;

import com.lolc.api.getway.dto.request.BillPaymentRequest;
import com.lolc.api.getway.dto.response.BillPaymentResponse;
import com.lolc.api.getway.entity.Account;
import com.lolc.api.getway.entity.Bill;
import com.lolc.api.getway.entity.BillPayment;
import com.lolc.api.getway.entity.Transaction;
import com.lolc.api.getway.enums.BillStatus;
import com.lolc.api.getway.enums.PaymentStatus;
import com.lolc.api.getway.enums.TransactionStatus;
import com.lolc.api.getway.enums.TransactionType;
import com.lolc.api.getway.exception.ConflictException;
import com.lolc.api.getway.exception.ResourceNotFoundException;
import com.lolc.api.getway.mapper.BillPaymentMapper;
import com.lolc.api.getway.repository.AccountRepository;
import com.lolc.api.getway.repository.BillRepository;
import com.lolc.api.getway.repository.BillPaymentRepository;
import com.lolc.api.getway.repository.TransactionRepository;
import com.lolc.api.getway.service.BillPaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BillPaymentServiceImpl implements BillPaymentService {
    private static final int MAX_RECEIPT_NO_RETRY = 3;

    private final BillPaymentRepository billPaymentRepository;
    private final BillRepository billRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BillPaymentMapper billPaymentMapper;

    @Override
    @Transactional
    public BillPaymentResponse createPay(BillPaymentRequest billPaymentRequest) {
        String normalizedAccountNumber = normalizeAccountNumber(billPaymentRequest.fromAccountNumber());
        String idempotencyKey = resolveIdempotencyKey(billPaymentRequest.idempotencyKey());

        Optional<BillPayment> existingPayment = billPaymentRepository.findByIdempotencyKey(idempotencyKey);
        if (existingPayment.isPresent()) {
            BillPayment payment = existingPayment.get();
            validateIdempotencyPayload(payment, billPaymentRequest, normalizedAccountNumber);
            return mapToResponse(payment);
        }

        Bill bill = billRepository.findByIdForUpdate(billPaymentRequest.billId()).orElseThrow(
                () -> new ResourceNotFoundException("Bill with id: " + billPaymentRequest.billId() + " not found")
        );

        Optional<BillPayment> existingPaymentAfterLock = billPaymentRepository.findByIdempotencyKey(idempotencyKey);
        if (existingPaymentAfterLock.isPresent()) {
            BillPayment payment = existingPaymentAfterLock.get();
            validateIdempotencyPayload(payment, billPaymentRequest, normalizedAccountNumber);
            return mapToResponse(payment);
        }

        if (bill.getStatus() == BillStatus.PAID) {
            throw new ConflictException("Bill has already been paid");
        }
        if (bill.getStatus() == BillStatus.CANCELLED) {
            throw new ConflictException("Cancelled bill cannot be paid");
        }
        if (billPaymentRequest.amount().compareTo(bill.getTotalAmount()) != 0) {
            throw new IllegalArgumentException("Payment amount must equal the bill total amount");
        }

        Account fromAccount = accountRepository.findByAccountNumber(normalizedAccountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("From account not found"));

        if (fromAccount.getBalance().compareTo(billPaymentRequest.amount()) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        BillPayment billPayment = new BillPayment();
        billPayment.setBill(bill);
        billPayment.setFromAccountNumber(normalizedAccountNumber);
        billPayment.setAmount(billPaymentRequest.amount());
        billPayment.setStatus(PaymentStatus.PENDING);
        billPayment.setReceiptNo(generateReceiptNo());
        billPayment.setIdempotencyKey(idempotencyKey);

        try {
            billPayment = reserveBillPaymentWithReceiptRetry(billPayment);
        } catch (DataIntegrityViolationException ex) {
            Optional<BillPayment> concurrentPayment = billPaymentRepository.findByIdempotencyKey(idempotencyKey);
            if (concurrentPayment.isPresent()) {
                BillPayment payment = concurrentPayment.get();
                validateIdempotencyPayload(payment, billPaymentRequest, normalizedAccountNumber);
                return mapToResponse(payment);
            }
            if (billPaymentRepository.findByBill_BillId(billPaymentRequest.billId()).isPresent()) {
                throw new ConflictException("Bill has already been paid");
            }
            throw ex;
        }

        Transaction transaction = new Transaction();
        transaction.setFromAccountId(fromAccount);
        transaction.setAmount(billPaymentRequest.amount());
        transaction.setType(TransactionType.BILL_PAYMENT);
        transaction.setDescription(buildDescription(bill));
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setReferenceNumber(System.currentTimeMillis());
        transaction = transactionRepository.save(transaction);

        fromAccount.setBalance(fromAccount.getBalance().subtract(billPaymentRequest.amount()));
        accountRepository.save(fromAccount);

        bill.setStatus(BillStatus.PAID);
        billRepository.save(bill);

        billPayment.setStatus(PaymentStatus.SUCCESS);
        billPayment.setTransactionId(String.valueOf(transaction.getTransactionId()));
        billPayment = billPaymentRepository.save(billPayment);

        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);

        return billPaymentMapper.toResponse(
                billPayment,
                fromAccount.getCurrency() == null ? null : fromAccount.getCurrency().name(),
                transaction.getReferenceNumber() == null ? null : String.valueOf(transaction.getReferenceNumber())
        );
    }

    private String normalizeAccountNumber(String accountNumber) {
        return accountNumber.replaceAll("\\s+", "");
    }

    private String resolveIdempotencyKey(String idempotencyKey) {
        if (!StringUtils.hasText(idempotencyKey)) {
            return generateIdempotencyKey();
        }
        return idempotencyKey.trim();
    }

    private String generateIdempotencyKey() {
        return "AUTO-" + UUID.randomUUID().toString().toUpperCase(Locale.ROOT);
    }

    private BillPayment reserveBillPaymentWithReceiptRetry(BillPayment billPayment) {
        for (int attempt = 0; attempt < MAX_RECEIPT_NO_RETRY; attempt++) {
            try {
                return billPaymentRepository.saveAndFlush(billPayment);
            } catch (DataIntegrityViolationException ex) {
                if (!isReceiptNumberConflict(ex)) {
                    throw ex;
                }
                billPayment.setReceiptNo(generateReceiptNo());
            }
        }
        throw new ConflictException("Unable to generate a unique receipt number");
    }

    private boolean isReceiptNumberConflict(DataIntegrityViolationException ex) {
        Throwable rootCause = ex.getMostSpecificCause();
        String errorMessage = rootCause == null ? ex.getMessage() : rootCause.getMessage();
        return errorMessage != null && errorMessage.toLowerCase(Locale.ROOT).contains("receipt_no");
    }

    private void validateIdempotencyPayload(
            BillPayment existingPayment,
            BillPaymentRequest request,
            String normalizedAccountNumber
    ) {
        boolean isSameBill = existingPayment.getBill() != null
                && existingPayment.getBill().getBillId() != null
                && existingPayment.getBill().getBillId().equals(request.billId());

        boolean isSameAmount = existingPayment.getAmount() != null
                && existingPayment.getAmount().compareTo(request.amount()) == 0;

        boolean isSameAccount = normalizedAccountNumber.equals(existingPayment.getFromAccountNumber());

        if (!isSameBill || !isSameAmount || !isSameAccount) {
            throw new ConflictException("Idempotency key already used with a different payment payload");
        }
    }

    private BillPaymentResponse mapToResponse(BillPayment billPayment) {
        String currency = accountRepository.findByAccountNumber(billPayment.getFromAccountNumber())
                .map(Account::getCurrency)
                .map(Enum::name)
                .orElse(null);

        String referenceNumber = null;
        if (StringUtils.hasText(billPayment.getTransactionId())) {
            try {
                Long transactionId = Long.parseLong(billPayment.getTransactionId());
                referenceNumber = transactionRepository.findById(transactionId)
                        .map(Transaction::getReferenceNumber)
                        .map(String::valueOf)
                        .orElse(null);
            } catch (NumberFormatException ignored) {
                referenceNumber = null;
            }
        }

        return billPaymentMapper.toResponse(billPayment, currency, referenceNumber);
    }

    private String buildDescription(Bill bill) {
        return "Bill payment " + bill.getBillType() + " " + bill.getBillCode();
    }

    private String generateReceiptNo() {
        return "BP-" + UUID.randomUUID().toString().replace("-", "").toUpperCase(Locale.ROOT).substring(0, 18);

    }
}
