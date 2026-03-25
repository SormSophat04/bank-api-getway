package com.lolc.api.getway.rest;

import com.lolc.api.getway.dto.request.LoanRequest;
import com.lolc.api.getway.dto.response.LoanResponse;
import com.lolc.api.getway.entity.Loan;
import com.lolc.api.getway.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    @PostMapping()
    public ResponseEntity<Loan> creatLoan(@Valid @RequestBody LoanRequest loanRequest){
        Loan loan = loanService.createLoan(loanRequest);
        return ResponseEntity.ok(loan);
    }

    @GetMapping
    public ResponseEntity<List<LoanResponse>> getLoans(){
        List<LoanResponse> loanAll = loanService.findLoanAll();
        return ResponseEntity.ok(loanAll);
    }

    @GetMapping("/{loanId}")
    public ResponseEntity<Loan> getLoanById(@PathVariable Long loanId){
        Loan loanById = loanService.findLoanById(loanId);
        return ResponseEntity.ok(loanById);
    }

    @PutMapping("/{loanId}")
    public ResponseEntity<Loan> updateLoan(@PathVariable Long loanId, @Valid @RequestBody LoanRequest loanRequest){
        Loan updateLoan = loanService.updateLoan(loanId, loanRequest);
        return ResponseEntity.ok(updateLoan);
    }

    @DeleteMapping("/{loanId}")
    public ResponseEntity<?> deleteLoan(@PathVariable Long loanId){
        loanService.delete(loanId);
        return ResponseEntity.ok().body("Loan has been deleted");
    }
}
