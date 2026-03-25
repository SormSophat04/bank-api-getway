package com.lolc.api.getway.impl;

import com.lolc.api.getway.dto.request.LoanRequest;
import com.lolc.api.getway.dto.response.LoanResponse;
import com.lolc.api.getway.entity.Loan;
import com.lolc.api.getway.exception.ResourceNotFoundException;
import com.lolc.api.getway.mapper.LoanMapper;
import com.lolc.api.getway.repository.CustomerRepository;
import com.lolc.api.getway.repository.LoanRepository;
import com.lolc.api.getway.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final LoanMapper loanMapper;

    // Calculate Loan
    public BigDecimal calculateEMI(double principal, double annualRate, int months) {

        double monthlyRate = annualRate / 12 / 100;

        return BigDecimal.valueOf((principal * monthlyRate * Math.pow(1 + monthlyRate, months))
                / (Math.pow(1 + monthlyRate, months) - 1));
    }

    @Override
    public Loan createLoan(LoanRequest loanRequest) {
        Loan loan = loanMapper.toEntity(loanRequest);
        loan.setCustomer(customerRepository.findById(loanRequest.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found " + loanRequest.customerId())));
        loan.setMonthlyPayment(calculateEMI(loanRequest.loanAmount().doubleValue(), loanRequest.interestRate(), loanRequest.durationMonths()));
        return loanRepository.save(loan);
    }

    @Override
    public List<LoanResponse> findLoanAll() {
        return loanRepository.findAll().stream().map(loanMapper::toResponse).toList();
    }

    @Override
    public Loan findLoanById(Long loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id " + loanId));
    }

    @Override
    public Loan updateLoan(Long loanId, LoanRequest loanRequest) {

        Loan loan = findLoanById(loanId);
        loan.setCustomer(customerRepository.findById(loanRequest.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found " + loanRequest.customerId())));
        loan.setLoanAmount(loanRequest.loanAmount());
        loan.setInterestRate(loanRequest.interestRate());
        loan.setDurationMonths(loanRequest.durationMonths());
        loan.setLoanStatus(loanRequest.loanStatus());
        return loanRepository.save(loan);
    }

    @Override
    public void delete(Long loanId) {
        loanRepository.delete(findLoanById(loanId));
    }
}
