package com.lolc.api.getway.impl;

import com.lolc.api.getway.dto.request.BillRequest;
import com.lolc.api.getway.dto.response.BillResponse;
import com.lolc.api.getway.entity.Bill;
import com.lolc.api.getway.enums.BillType;
import com.lolc.api.getway.mapper.BillMapper;
import com.lolc.api.getway.repository.BillRepository;
import com.lolc.api.getway.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;
    private final BillMapper billMapper;

    @Override
    public BillResponse findRecept(BillType billTypes, String billCode) {
        Optional<Bill> byBillTypeAndBillCode = billRepository.findByBillTypeAndBillCode(billTypes, billCode);
        return byBillTypeAndBillCode.map(billMapper::toResponse).orElse(null);
    }

    @Override
    public List<BillResponse> findAll() {
        List<Bill> bills = billRepository.findAll();
        return bills.stream()
                .map(billMapper::toResponse)
                .toList();
    }

    @Override
    public List<BillResponse> findByCustomerId(Long customerId) {
        return List.of();
    }

    @Override
    public BillResponse findById(Long billId) {
        return null;
    }
}
