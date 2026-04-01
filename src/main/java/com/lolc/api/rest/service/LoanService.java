package com.lolc.api.rest.service;

import com.lolc.api.rest.dto.request.LoanRequest;
import com.lolc.api.rest.dto.response.LoanResponse;

import java.util.List;

public interface LoanService {
    LoanResponse createLoan(LoanRequest loanRequest);
    LoanResponse loanCalculator(LoanRequest loanRequest);
    List<LoanResponse> findLoanAll();
    List<LoanResponse> findLoanByCustomerId(Long customerId);
    LoanResponse findLoanById(Long loanId);
    LoanResponse updateLoan(Long loanId, LoanRequest loanRequest);
    byte[] exportLoanToExcelById(Long loanId);
    byte[] exportLoansToExcelByMonth(int year, int month);
    void delete(Long loanId);
}
