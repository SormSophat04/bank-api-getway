package com.lolc.api.rest.impl;

import com.lolc.api.rest.dto.request.LoanRequest;
import com.lolc.api.rest.dto.response.LoanResponse;
import com.lolc.api.rest.entity.Customer;
import com.lolc.api.rest.entity.Loan;
import com.lolc.api.rest.enums.Currency;
import com.lolc.api.rest.exception.ResourceNotFoundException;
import com.lolc.api.rest.mapper.LoanMapper;
import com.lolc.api.rest.repository.CustomerRepository;
import com.lolc.api.rest.repository.LoanRepository;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private LoanMapper loanMapper;

    @InjectMocks
    private LoanServiceImpl loanService;

    @Test
    void createLoanShouldCalculateTotalRepaymentAndInterest() {
        LoanRequest request = new LoanRequest(
                1L,
                new BigDecimal("1000"),
                Currency.USD,
                12.0,
                12,
                "APPROVED"
        );

        Customer customer = new Customer();
        customer.setCustomerId(1L);

        Loan loanEntity = new Loan();
        loanEntity.setLoanAmount(request.loanAmount());
        loanEntity.setCurrency(request.currency());
        loanEntity.setInterestRate(request.interestRate());
        loanEntity.setDurationMonths(request.durationMonths());
        loanEntity.setLoanStatus(request.loanStatus());

        when(loanMapper.toEntity(request)).thenReturn(loanEntity);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(loanMapper.toResponse(any(Loan.class))).thenAnswer(invocation -> {
            Loan saved = invocation.getArgument(0);
            return new LoanResponse(
                    saved.getLoanId(),
                    null,
                    saved.getLoanAmount(),
                    saved.getCurrency(),
                    saved.getInterestRate(),
                    saved.getDurationMonths(),
                    saved.getMonthlyPayment(),
                    saved.getPrincipal(),
                    saved.getTotalInterest(),
                    saved.getTotalRepayment(),
                    null,
                    null
            );
        });

        LoanResponse response = loanService.createLoan(request);

        ArgumentCaptor<Loan> loanCaptor = ArgumentCaptor.forClass(Loan.class);
        verify(loanRepository).save(loanCaptor.capture());
        Loan savedLoan = loanCaptor.getValue();

        assertEquals(new BigDecimal("1000.00"), savedLoan.getPrincipal());
        assertEquals(new BigDecimal("88.85"), savedLoan.getMonthlyPayment());
        assertEquals(new BigDecimal("1066.20"), savedLoan.getTotalRepayment());
        assertEquals(new BigDecimal("66.20"), savedLoan.getTotalInterest());
        assertEquals(savedLoan.getTotalRepayment(), response.totalRepayment());
    }

    @Test
    void exportLoansToExcelByMonthShouldLoopMonthData() throws Exception {
        Customer customer = new Customer();
        customer.setCustomerId(2L);
        customer.setFirstName("Chan");
        customer.setLastName("Dara");

        Loan loan = new Loan();
        loan.setLoanId(101L);
        loan.setCustomer(customer);
        loan.setLoanAmount(new BigDecimal("2000.00"));
        loan.setCurrency(Currency.USD);
        loan.setInterestRate(12.0);
        loan.setDurationMonths(24);
        loan.setMonthlyPayment(new BigDecimal("94.15"));
        loan.setPrincipal(new BigDecimal("2000.00"));
        loan.setTotalInterest(new BigDecimal("259.60"));
        loan.setTotalRepayment(new BigDecimal("2259.60"));
        loan.setLoanStatus("APPROVED");
        loan.setCreateAt(LocalDateTime.of(2026, 4, 15, 10, 30));

        when(loanRepository.findByCreateAtGreaterThanEqualAndCreateAtLessThanOrderByCreateAtAsc(any(), any()))
                .thenReturn(List.of(loan));

        byte[] result = loanService.exportLoansToExcelByMonth(2026, 4);
        assertNotNull(result);
        assertTrue(result.length > 0);

        ArgumentCaptor<LocalDateTime> startCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> endCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(loanRepository).findByCreateAtGreaterThanEqualAndCreateAtLessThanOrderByCreateAtAsc(
                startCaptor.capture(),
                endCaptor.capture()
        );

        assertEquals(LocalDateTime.of(2026, 4, 1, 0, 0), startCaptor.getValue());
        assertEquals(LocalDateTime.of(2026, 5, 1, 0, 0), endCaptor.getValue());

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(result))) {
            Sheet sheet = workbook.getSheetAt(0);
            assertEquals("Loan ID", sheet.getRow(0).getCell(0).getStringCellValue());
            assertEquals("101", sheet.getRow(1).getCell(0).getStringCellValue());
            assertEquals("2", sheet.getRow(1).getCell(1).getStringCellValue());
            assertEquals("Chan Dara", sheet.getRow(1).getCell(2).getStringCellValue());
            assertEquals("2259.60", sheet.getRow(1).getCell(10).getStringCellValue());
        }
    }

    @Test
    void exportLoansToExcelByMonthShouldRejectInvalidMonth() {
        assertThrows(IllegalArgumentException.class, () -> loanService.exportLoansToExcelByMonth(2026, 13));
    }

    @Test
    void exportLoanToExcelByIdShouldReturnOneLoan() throws Exception {
        Customer customer = new Customer();
        customer.setCustomerId(3L);
        customer.setFirstName("Sok");
        customer.setLastName("Piseth");

        Loan loan = new Loan();
        loan.setLoanId(202L);
        loan.setCustomer(customer);
        loan.setLoanAmount(new BigDecimal("5000.00"));
        loan.setCurrency(Currency.KHR);
        loan.setInterestRate(10.5);
        loan.setDurationMonths(18);
        loan.setMonthlyPayment(new BigDecimal("302.78"));
        loan.setPrincipal(new BigDecimal("5000.00"));
        loan.setTotalInterest(new BigDecimal("450.04"));
        loan.setTotalRepayment(new BigDecimal("5450.04"));
        loan.setLoanStatus("APPROVED");
        loan.setCreateAt(LocalDateTime.of(2026, 4, 1, 9, 0));

        when(loanRepository.findById(202L)).thenReturn(Optional.of(loan));

        byte[] result = loanService.exportLoanToExcelById(202L);
        assertNotNull(result);
        assertTrue(result.length > 0);
        verify(loanRepository).findById(202L);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(result))) {
            Sheet sheet = workbook.getSheetAt(0);
            assertEquals("Loan-202", sheet.getSheetName());
            assertEquals("202", sheet.getRow(1).getCell(0).getStringCellValue());
            assertEquals("Sok Piseth", sheet.getRow(1).getCell(2).getStringCellValue());
            assertEquals("5450.04", sheet.getRow(1).getCell(10).getStringCellValue());
        }
    }

    @Test
    void exportLoanToExcelByIdShouldThrowWhenLoanNotFound() {
        when(loanRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> loanService.exportLoanToExcelById(999L));
    }
}
