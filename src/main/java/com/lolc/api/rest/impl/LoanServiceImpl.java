package com.lolc.api.rest.impl;

import com.lolc.api.rest.dto.request.LoanRequest;
import com.lolc.api.rest.dto.response.LoanResponse;
import com.lolc.api.rest.entity.Customer;
import com.lolc.api.rest.entity.Loan;
import com.lolc.api.rest.exception.ResourceNotFoundException;
import com.lolc.api.rest.mapper.LoanMapper;
import com.lolc.api.rest.repository.CustomerRepository;
import com.lolc.api.rest.repository.LoanRepository;
import com.lolc.api.rest.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private static final DateTimeFormatter EXPORT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final LoanMapper loanMapper;

    // Calculate Loan
    public BigDecimal calculateEMI(double principal, double annualRate, int months) {

        double monthlyRate = annualRate / 12 / 100;

        double emi = (principal * monthlyRate * Math.pow(1 + monthlyRate, months))
                / (Math.pow(1 + monthlyRate, months) - 1);
        return BigDecimal.valueOf(emi).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTotalRepayment(BigDecimal monthlyPayment, int months) {
        return monthlyPayment.multiply(BigDecimal.valueOf(months))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTotalInterest(BigDecimal totalRepayment, BigDecimal principal) {
        return totalRepayment.subtract(principal).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public LoanResponse createLoan(LoanRequest loanRequest) {
        Loan loan = loanMapper.toEntity(loanRequest);
        BigDecimal principal = loanRequest.loanAmount().setScale(2, RoundingMode.HALF_UP);
        loan.setCustomer(customerRepository.findById(loanRequest.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found " + loanRequest.customerId())));
        loan.setPrincipal(principal);

        BigDecimal monthlyPayment = calculateEMI(loanRequest.loanAmount().doubleValue(), loanRequest.interestRate(), loanRequest.durationMonths());
        BigDecimal totalRepayment = calculateTotalRepayment(monthlyPayment, loanRequest.durationMonths());

        loan.setMonthlyPayment(monthlyPayment);
        loan.setTotalRepayment(totalRepayment);
        loan.setTotalInterest(calculateTotalInterest(totalRepayment, principal));

        return loanMapper.toResponse(loanRepository.save(loan));
    }

    @Override
    public LoanResponse loanCalculator(LoanRequest loanRequest) {
        Loan loanMapperEntity = loanMapper.toEntity(loanRequest);

        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanResponse> findLoanAll() {
        return loanRepository.findAll().stream().map(loanMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanResponse> findLoanByCustomerId(Long customerId) {
        return loanRepository.findLoansByCustomer_CustomerId(customerId)
                .stream().map(loanMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LoanResponse findLoanById(Long loanId) {
        return loanRepository.findById(loanId).stream().map(loanMapper::toResponse)
                .findFirst().orElseThrow(() -> new ResourceNotFoundException("Loan not found " + loanId));
    }

    @Override
    public LoanResponse updateLoan(Long loanId, LoanRequest loanRequest) {
        LoanResponse loanById = findLoanById(loanId);
        Loan loan = loanMapper.updateLoan(loanById.loanId(), loanRequest);
        return loanMapper.toResponse(loanRepository.save(loan));
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportLoanToExcelById(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found " + loanId));
        return buildLoanWorkbook(List.of(loan), "Loan-" + loanId);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportLoansToExcelByMonth(int year, int month) {
        YearMonth yearMonth = parseYearMonth(year, month);
        LocalDateTime startDateTime = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDateTime = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        List<Loan> loans = loanRepository.findByCreateAtGreaterThanEqualAndCreateAtLessThanOrderByCreateAtAsc(
                startDateTime,
                endDateTime
        );

        return buildLoanWorkbook(loans, "Loans-" + yearMonth);
    }

    @Override
    public void delete(Long loanId) {
        loanRepository.deleteById(loanId);
    }

    private YearMonth parseYearMonth(int year, int month) {
        try {
            return YearMonth.of(year, month);
        } catch (DateTimeException ex) {
            throw new IllegalArgumentException("Invalid year/month. Example: year=2026&month=4");
        }
    }

    private byte[] buildLoanWorkbook(List<Loan> loans, String sheetName) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(sheetName);
            String[] columns = {
                    "Loan ID",
                    "Customer ID",
                    "Customer Name",
                    "Loan Amount",
                    "Currency",
                    "Interest Rate",
                    "Duration (Months)",
                    "Monthly Payment",
                    "Principal",
                    "Total Interest",
                    "Total Repayment",
                    "Status",
                    "Created At"
            };

            Row header = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            int rowIndex = 1;
            for (Loan loan : loans) {
                Row row = sheet.createRow(rowIndex++);
                Customer customer = loan.getCustomer();

                row.createCell(0).setCellValue(loan.getLoanId() == null ? "" : String.valueOf(loan.getLoanId()));
                row.createCell(1).setCellValue(
                        customer == null || customer.getCustomerId() == null ? "" : String.valueOf(customer.getCustomerId())
                );
                row.createCell(2).setCellValue(buildCustomerName(customer));
                row.createCell(3).setCellValue(toText(loan.getLoanAmount()));
                row.createCell(4).setCellValue(loan.getCurrency() == null ? "" : loan.getCurrency().name());
                row.createCell(5).setCellValue(loan.getInterestRate() == null ? "" : loan.getInterestRate().toString());
                row.createCell(6).setCellValue(
                        loan.getDurationMonths() == null ? "" : String.valueOf(loan.getDurationMonths())
                );
                row.createCell(7).setCellValue(toText(loan.getMonthlyPayment()));
                row.createCell(8).setCellValue(toText(loan.getPrincipal()));
                row.createCell(9).setCellValue(toText(loan.getTotalInterest()));
                row.createCell(10).setCellValue(toText(loan.getTotalRepayment()));
                row.createCell(11).setCellValue(loan.getLoanStatus() == null ? "" : loan.getLoanStatus());
                row.createCell(12).setCellValue(
                        loan.getCreateAt() == null ? "" : loan.getCreateAt().format(EXPORT_DATE_TIME_FORMATTER)
                );
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to export loans to Excel", ex);
        }
    }

    private String toText(BigDecimal value) {
        return value == null ? "" : value.toPlainString();
    }

    private String buildCustomerName(Customer customer) {
        if (customer == null) {
            return "";
        }

        String firstName = customer.getFirstName() == null ? "" : customer.getFirstName().trim();
        String lastName = customer.getLastName() == null ? "" : customer.getLastName().trim();
        return (firstName + " " + lastName).trim();
    }
}
