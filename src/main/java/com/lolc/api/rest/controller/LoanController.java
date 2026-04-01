package com.lolc.api.rest.controller;

import com.lolc.api.rest.dto.request.LoanRequest;
import com.lolc.api.rest.dto.response.LoanResponse;
import com.lolc.api.rest.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    @PostMapping()
    public ResponseEntity<LoanResponse> creatLoan(@Valid @RequestBody LoanRequest loanRequest){
        LoanResponse loan = loanService.createLoan(loanRequest);
        return ResponseEntity.ok(loan);
    }

    @GetMapping
    public ResponseEntity<List<LoanResponse>> getLoans(){
        List<LoanResponse> loanAll = loanService.findLoanAll();
        return ResponseEntity.ok(loanAll);
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportLoansByMonth(
            @RequestParam int year,
            @RequestParam int month
    ) {
        byte[] excel = loanService.exportLoansToExcelByMonth(year, month);
        String fileName = "loans-" + year + "-" + String.format("%02d", month) + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }

    @GetMapping("/{loanId}/export/excel")
    public ResponseEntity<byte[]> exportLoanById(@PathVariable Long loanId) {
        byte[] excel = loanService.exportLoanToExcelById(loanId);
        String fileName = "loan-" + loanId + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<LoanResponse>> getLoanByCustomer(@PathVariable Long customerId){
        List<LoanResponse> loanByCustomerId = loanService.findLoanByCustomerId(customerId);
        return ResponseEntity.ok(loanByCustomerId);
    }

    @GetMapping("/{loanId}")
    public ResponseEntity<LoanResponse> getLoanById(@PathVariable Long loanId){
        LoanResponse loanById = loanService.findLoanById(loanId);
        return ResponseEntity.ok(loanById);
    }

    @PutMapping("/{loanId}")
    public ResponseEntity<LoanResponse> updateLoan(@PathVariable Long loanId, @Valid @RequestBody LoanRequest loanRequest){
        LoanResponse loanResponse = loanService.updateLoan(loanId, loanRequest);
        return ResponseEntity.ok(loanResponse);
    }

    @DeleteMapping("/{loanId}")
    public ResponseEntity<?> deleteLoan(@PathVariable Long loanId){
        loanService.delete(loanId);
        return ResponseEntity.ok().body("Loan has been deleted");
    }
}
