package com.lolc.api.rest.controller;

import com.lolc.api.rest.dto.request.BillPaymentRequest;
import com.lolc.api.rest.dto.response.BillPaymentResponse;
import com.lolc.api.rest.service.BillPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bill-payments")
public class BillPaymentController {

    private final BillPaymentService billPaymentService;

    @PostMapping
    public ResponseEntity<BillPaymentResponse> createBillPayment(@Valid @RequestBody BillPaymentRequest request) {
        return ResponseEntity.ok(billPaymentService.createPay(request));
    }
}
