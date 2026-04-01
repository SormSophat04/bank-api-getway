package com.lolc.api.rest.service;

import com.lolc.api.rest.dto.request.BillPaymentRequest;
import com.lolc.api.rest.dto.response.BillPaymentResponse;

public interface BillPaymentService {
    BillPaymentResponse createPay(BillPaymentRequest billPaymentRequest);
}