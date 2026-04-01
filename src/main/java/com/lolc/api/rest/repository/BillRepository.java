package com.lolc.api.rest.repository;

import com.lolc.api.rest.entity.Bill;
import com.lolc.api.rest.enums.BillType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {
    Optional<Bill> findByBillTypeAndBillCode(BillType billType, String billCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Bill b where b.billId = :billId")
    Optional<Bill> findByIdForUpdate(@Param("billId") Long billId);
}
