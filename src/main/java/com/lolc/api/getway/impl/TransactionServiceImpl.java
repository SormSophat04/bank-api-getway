package com.lolc.api.getway.impl;

import com.lolc.api.getway.dto.TransactionDTO;
import com.lolc.api.getway.dto.request.TransferRequest;
import com.lolc.api.getway.dto.response.TransactionResponse;
import com.lolc.api.getway.entity.Account;
import com.lolc.api.getway.entity.Transaction;
import com.lolc.api.getway.enums.TransactionStatus;
import com.lolc.api.getway.exception.ResourceNotFoundException;
import com.lolc.api.getway.mapper.TransactionMapper;
import com.lolc.api.getway.repository.AccountRepository;
import com.lolc.api.getway.repository.TransactionRepository;
import com.lolc.api.getway.service.TransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public Transaction create(TransactionDTO transactionDTO) {
        Transaction transaction = transactionMapper.toTransaction(transactionDTO);
        return transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public List<TransactionResponse> list() {
        return transactionRepository.findAll().stream()
                .map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Transaction findById(Long transactionId) {
        return transactionRepository.findById(transactionId).orElseThrow(
                () -> new ResourceNotFoundException("Transaction with id: " + transactionId + " not found")
        );
    }

    @Override
    @Transactional
    public TransactionResponse findResponseById(Long transactionId) {
        return transactionMapper.toResponse(findById(transactionId));
    }

    @Override
    public Transaction update(Long transactionId, TransactionDTO transactionDTO) {
        Transaction byId = findById(transactionId);
        byId = transactionMapper.toTransaction(transactionDTO);
        return transactionRepository.save(byId);
    }

    @Override
    public void delete(Long transactionId) {
        Transaction byId = findById(transactionId);
        transactionRepository.delete(byId);
    }

    @Override
    @Transactional
    public TransactionResponse transfer(TransferRequest request) {

        // Validation
        if (!StringUtils.hasText(request.fromAccountNumber()) || !StringUtils.hasText(request.toAccountNumber())) {
            throw new RuntimeException("Account number is required");
        }
        if (request.fromAccountNumber().equals(request.toAccountNumber())) {
            throw new RuntimeException("Cannot transfer to the same account");
        }
        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }

        // Normalize account numbers
        String fromAccountNumber = request.fromAccountNumber().replaceAll("\\s+", "");
        String toAccountNumber = request.toAccountNumber().replaceAll("\\s+", "");



        // Lock Account
        Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
                .orElseThrow(() -> new RuntimeException("From account not found"));
        Account toAccount = accountRepository.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new RuntimeException("To account not found"));

        // Check Balance
        if (fromAccount.getBalance().compareTo(request.amount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        Transaction transaction = transactionMapper.toEntity(request);
        transaction.setFromAccountId(fromAccount);
        transaction.setToAccountId(toAccount);
        transaction.setReferenceNumber(Long.valueOf(generateReference()));

        transactionRepository.save(transaction);

        // Deduct
        fromAccount.setBalance(fromAccount.getBalance().subtract(request.amount()));

        // Add (with currency conversion)
        BigDecimal convertedAmount = convertCurrency(request.amount(), fromAccount.getCurrency().name(), toAccount.getCurrency().name());

        toAccount.setBalance(toAccount.getBalance().add(convertedAmount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);

        return transactionMapper.toResponse(transaction);
    }

    @Override
    @Transactional
    public List<TransactionResponse> getByAccountId(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account with id: " + accountId + " not found"));
        List<Transaction> transactions = transactionRepository.findByFromAccountIdOrToAccountIdOrderByCreateAtDesc(account, account);
        
        return transactions.stream()
                .map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    private String generateReference() {
        return String.valueOf(System.currentTimeMillis());
    }

    private BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }
        // Example conversion logic: USD to KHR (simplified)
        if ("USD".equals(fromCurrency) && "KHR".equals(toCurrency)) {
            return amount.multiply(new BigDecimal("4100")).setScale(2, RoundingMode.HALF_UP);
        }
        if ("KHR".equals(fromCurrency) && "USD".equals(toCurrency)) {
            return amount.divide(new BigDecimal("4100"), 2, RoundingMode.HALF_UP);
        }
        return amount;
    }
}
