package com.lolc.api.getway.service;

import com.lolc.api.getway.dto.request.BillRequest;
import com.lolc.api.getway.dto.response.BillResponse;
import com.lolc.api.getway.enums.BillType;

import java.util.List;

public interface BillService {
    BillResponse findRecept(BillType billTypes, String billCode);
    List<BillResponse> findAll();
    List<BillResponse> findByCustomerId(Long customerId);
    BillResponse findById(Long billId);
}
