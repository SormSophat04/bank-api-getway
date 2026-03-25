package com.lolc.api.getway.repository;

import com.lolc.api.getway.entity.Bill;
import com.lolc.api.getway.enums.BillType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {
    Optional<Bill> findByBillTypeAndBillCode(BillType billType, String billCode);
}
