package com.lolc.api.getway.service;

import com.lolc.api.getway.dto.request.BillPaymentRequest;
import com.lolc.api.getway.dto.response.BillPaymentResponse;

public interface BillPaymentService {
    BillPaymentResponse createPay(BillPaymentRequest billPaymentRequest);
}