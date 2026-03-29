package com.lolc.api.getway.impl;

import com.lolc.api.getway.dto.request.BillPaymentRequest;
import com.lolc.api.getway.dto.response.BillPaymentResponse;
import com.lolc.api.getway.entity.Account;
import com.lolc.api.getway.entity.Bill;
import com.lolc.api.getway.entity.BillPayment;
import com.lolc.api.getway.entity.Transaction;
import com.lolc.api.getway.enums.BillStatus;
import com.lolc.api.getway.enums.BillType;
import com.lolc.api.getway.enums.Currency;
import com.lolc.api.getway.enums.PaymentStatus;
import com.lolc.api.getway.enums.TransactionStatus;
import com.lolc.api.getway.mapper.BillPaymentMapper;
import com.lolc.api.getway.repository.AccountRepository;
import com.lolc.api.getway.repository.BillPaymentRepository;
import com.lolc.api.getway.repository.BillRepository;
import com.lolc.api.getway.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BillPaymentServiceImplTest {

    @Mock
    private BillPaymentRepository billPaymentRepository;

    @Mock
    private BillRepository billRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BillPaymentMapper billPaymentMapper;

    @InjectMocks
    private BillPaymentServiceImpl billPaymentService;

    @Test
    void createPayShouldProcessBillPaymentAndDeductBalance() {
        BillPaymentRequest request = new BillPaymentRequest(
                10L,
                "001 234",
                new BigDecimal("25.00"),
                "idem-001"
        );

        Bill bill = new Bill();
        bill.setBillId(10L);
        bill.setBillType(BillType.ELECTRIC);
        bill.setBillCode("BILL-001");
        bill.setStatus(BillStatus.UNPAID);
        bill.setTotalAmount(new BigDecimal("25.00"));

        Account account = new Account();
        account.setAccountNumber("001234");
        account.setBalance(new BigDecimal("100.00"));
        account.setCurrency(Currency.USD);

        when(billPaymentRepository.findByIdempotencyKey("idem-001")).thenReturn(Optional.empty());
        when(billRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(bill));
        when(accountRepository.findByAccountNumber("001234")).thenReturn(Optional.of(account));

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction tx = invocation.getArgument(0);
            if (tx.getTransactionId() == null) {
                tx.setTransactionId(99L);
            }
            return tx;
        });

        when(billPaymentRepository.saveAndFlush(any(BillPayment.class))).thenAnswer(invocation -> {
            BillPayment payment = invocation.getArgument(0);
            payment.setBillPaymentId(500L);
            return payment;
        });

        when(billPaymentRepository.save(any(BillPayment.class))).thenAnswer(invocation -> {
            BillPayment payment = invocation.getArgument(0);
            payment.setBillPaymentId(500L);
            return payment;
        });

        BillPaymentResponse expected = new BillPaymentResponse(
                500L,
                10L,
                99L,
                "RCPT-1",
                PaymentStatus.SUCCESS,
                new BigDecimal("25.00"),
                "USD",
                "1234567890",
                null
        );
        when(billPaymentMapper.toResponse(any(BillPayment.class), eq("USD"), anyString())).thenReturn(expected);

        BillPaymentResponse response = billPaymentService.createPay(request);

        assertSame(expected, response);
        assertEquals(new BigDecimal("75.00"), account.getBalance());
        assertEquals(BillStatus.PAID, bill.getStatus());

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(2)).save(transactionCaptor.capture());
        assertEquals(TransactionStatus.SUCCESS, transactionCaptor.getAllValues().getLast().getStatus());
    }

    @Test
    void createPayShouldReturnExistingPaymentForSameIdempotencyKey() {
        BillPaymentRequest request = new BillPaymentRequest(
                20L,
                "ACC-001",
                new BigDecimal("10.00"),
                "idem-002"
        );

        Bill bill = new Bill();
        bill.setBillId(20L);

        BillPayment existing = new BillPayment();
        existing.setBill(bill);
        existing.setAmount(new BigDecimal("10.00"));
        existing.setFromAccountNumber("ACC-001");
        existing.setTransactionId("77");

        Account account = new Account();
        account.setCurrency(Currency.KHR);

        Transaction transaction = new Transaction();
        transaction.setReferenceNumber(8888L);

        when(billPaymentRepository.findByIdempotencyKey("idem-002")).thenReturn(Optional.of(existing));
        when(accountRepository.findByAccountNumber("ACC-001")).thenReturn(Optional.of(account));
        when(transactionRepository.findById(77L)).thenReturn(Optional.of(transaction));

        BillPaymentResponse expected = new BillPaymentResponse(
                1L,
                20L,
                77L,
                "RCPT-2",
                PaymentStatus.SUCCESS,
                new BigDecimal("10.00"),
                "KHR",
                "8888",
                null
        );
        when(billPaymentMapper.toResponse(existing, "KHR", "8888")).thenReturn(expected);

        BillPaymentResponse response = billPaymentService.createPay(request);

        assertSame(expected, response);
        verify(billRepository, never()).findById(any());
        verify(billRepository, never()).findByIdForUpdate(any());
        verify(billPaymentRepository, never()).saveAndFlush(any());
        verify(billPaymentRepository, never()).save(any());
    }

    @Test
    void createPayShouldRejectAmountMismatch() {
        BillPaymentRequest request = new BillPaymentRequest(
                30L,
                "ACC-123",
                new BigDecimal("19.00"),
                "idem-003"
        );

        Bill bill = new Bill();
        bill.setBillId(30L);
        bill.setStatus(BillStatus.UNPAID);
        bill.setTotalAmount(new BigDecimal("20.00"));

        when(billPaymentRepository.findByIdempotencyKey("idem-003")).thenReturn(Optional.empty());
        when(billRepository.findByIdForUpdate(30L)).thenReturn(Optional.of(bill));

        assertThrows(IllegalArgumentException.class, () -> billPaymentService.createPay(request));
        verify(accountRepository, never()).findByAccountNumber(any());
    }

    @Test
    void createPayShouldAutoGenerateIdempotencyKeyWhenMissing() {
        BillPaymentRequest request = new BillPaymentRequest(
                60L,
                "001 999",
                new BigDecimal("15.00"),
                null
        );

        Bill bill = new Bill();
        bill.setBillId(60L);
        bill.setBillType(BillType.MOBILE);
        bill.setBillCode("BILL-060");
        bill.setStatus(BillStatus.UNPAID);
        bill.setTotalAmount(new BigDecimal("15.00"));

        Account account = new Account();
        account.setAccountNumber("001999");
        account.setBalance(new BigDecimal("100.00"));
        account.setCurrency(Currency.USD);

        when(billPaymentRepository.findByIdempotencyKey(anyString())).thenReturn(Optional.empty());
        when(billRepository.findByIdForUpdate(60L)).thenReturn(Optional.of(bill));
        when(accountRepository.findByAccountNumber("001999")).thenReturn(Optional.of(account));

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction tx = invocation.getArgument(0);
            if (tx.getTransactionId() == null) {
                tx.setTransactionId(160L);
            }
            return tx;
        });

        when(billPaymentRepository.saveAndFlush(any(BillPayment.class))).thenAnswer(invocation -> {
            BillPayment payment = invocation.getArgument(0);
            payment.setBillPaymentId(600L);
            return payment;
        });

        when(billPaymentRepository.save(any(BillPayment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BillPaymentResponse expected = new BillPaymentResponse(
                600L,
                60L,
                160L,
                "RCPT-60",
                PaymentStatus.SUCCESS,
                new BigDecimal("15.00"),
                "USD",
                "160001",
                null
        );
        when(billPaymentMapper.toResponse(any(BillPayment.class), eq("USD"), anyString())).thenReturn(expected);

        BillPaymentResponse response = billPaymentService.createPay(request);
        assertSame(expected, response);

        ArgumentCaptor<BillPayment> paymentCaptor = ArgumentCaptor.forClass(BillPayment.class);
        verify(billPaymentRepository).saveAndFlush(paymentCaptor.capture());

        String generatedKey = paymentCaptor.getValue().getIdempotencyKey();
        assertNotNull(generatedKey);
        assertTrue(generatedKey.startsWith("AUTO-"));
    }

    @Test
    void createPayShouldReturnExistingPaymentWhenConcurrentIdempotencyInsertConflicts() {
        BillPaymentRequest request = new BillPaymentRequest(
                40L,
                "ACC-500",
                new BigDecimal("30.00"),
                "idem-race"
        );

        Bill bill = new Bill();
        bill.setBillId(40L);
        bill.setBillType(BillType.WATER);
        bill.setBillCode("BILL-040");
        bill.setStatus(BillStatus.UNPAID);
        bill.setTotalAmount(new BigDecimal("30.00"));

        Account account = new Account();
        account.setCurrency(Currency.USD);
        account.setBalance(new BigDecimal("100.00"));

        BillPayment existing = new BillPayment();
        existing.setBill(bill);
        existing.setAmount(new BigDecimal("30.00"));
        existing.setFromAccountNumber("ACC-500");
        existing.setTransactionId("120");

        Transaction transaction = new Transaction();
        transaction.setReferenceNumber(7777L);

        when(billPaymentRepository.findByIdempotencyKey("idem-race"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(existing));
        when(billRepository.findByIdForUpdate(40L)).thenReturn(Optional.of(bill));
        when(accountRepository.findByAccountNumber("ACC-500")).thenReturn(Optional.of(account));
        when(billPaymentRepository.saveAndFlush(any(BillPayment.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate idempotency_key"));
        when(transactionRepository.findById(120L)).thenReturn(Optional.of(transaction));

        BillPaymentResponse expected = new BillPaymentResponse(
                9L,
                40L,
                120L,
                "RCPT-9",
                PaymentStatus.SUCCESS,
                new BigDecimal("30.00"),
                "USD",
                "7777",
                null
        );
        when(billPaymentMapper.toResponse(existing, "USD", "7777")).thenReturn(expected);

        BillPaymentResponse response = billPaymentService.createPay(request);

        assertSame(expected, response);
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(accountRepository, never()).save(any(Account.class));
        verify(billRepository, never()).save(any(Bill.class));
        verify(billPaymentRepository, never()).save(any(BillPayment.class));
    }

    @Test
    void createPayShouldReturnExistingPaymentWhenKeyAppearsAfterBillLock() {
        BillPaymentRequest request = new BillPaymentRequest(
                50L,
                "ACC-777",
                new BigDecimal("12.00"),
                "idem-after-lock"
        );

        Bill bill = new Bill();
        bill.setBillId(50L);
        bill.setStatus(BillStatus.PAID);

        Bill paymentBill = new Bill();
        paymentBill.setBillId(50L);

        BillPayment existing = new BillPayment();
        existing.setBill(paymentBill);
        existing.setAmount(new BigDecimal("12.00"));
        existing.setFromAccountNumber("ACC-777");
        existing.setTransactionId("321");

        Account account = new Account();
        account.setCurrency(Currency.USD);

        Transaction transaction = new Transaction();
        transaction.setReferenceNumber(9999L);

        when(billPaymentRepository.findByIdempotencyKey("idem-after-lock"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(existing));
        when(billRepository.findByIdForUpdate(50L)).thenReturn(Optional.of(bill));
        when(accountRepository.findByAccountNumber("ACC-777")).thenReturn(Optional.of(account));
        when(transactionRepository.findById(321L)).thenReturn(Optional.of(transaction));

        BillPaymentResponse expected = new BillPaymentResponse(
                11L,
                50L,
                321L,
                "RCPT-11",
                PaymentStatus.SUCCESS,
                new BigDecimal("12.00"),
                "USD",
                "9999",
                null
        );
        when(billPaymentMapper.toResponse(existing, "USD", "9999")).thenReturn(expected);

        BillPaymentResponse response = billPaymentService.createPay(request);

        assertSame(expected, response);
        verify(billPaymentRepository, never()).saveAndFlush(any(BillPayment.class));
        verify(billPaymentRepository, never()).save(any(BillPayment.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}
