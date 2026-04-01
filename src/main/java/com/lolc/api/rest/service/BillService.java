package com.lolc.api.rest.service;

import com.lolc.api.rest.dto.response.BillResponse;
import com.lolc.api.rest.enums.BillType;

import java.util.List;

public interface BillService {
    BillResponse findRecept(BillType billTypes, String billCode);
    List<BillResponse> findAll();
    List<BillResponse> findByCustomerId(Long customerId);
    BillResponse findById(Long billId);
}
