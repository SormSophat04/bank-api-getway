package com.lolc.api.getway.repository;

import com.lolc.api.getway.entity.BillPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BillPaymentRepository extends JpaRepository<BillPayment, Long> {
    Optional<BillPayment> findByIdempotencyKey(String idempotencyKey);

    Optional<BillPayment> findByBill_BillId(Long billId);
}
