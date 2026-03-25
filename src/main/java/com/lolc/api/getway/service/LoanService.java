package com.lolc.api.getway.service;

import com.lolc.api.getway.dto.request.LoanRequest;
import com.lolc.api.getway.dto.response.LoanResponse;
import com.lolc.api.getway.entity.Loan;

import java.util.List;

public interface LoanService {
    Loan createLoan(LoanRequest loanRequest);
    List<LoanResponse> findLoanAll();
    Loan findLoanById(Long loanId);
    Loan updateLoan(Long loanId, LoanRequest loanRequest);
    void delete(Long loanId);
}
