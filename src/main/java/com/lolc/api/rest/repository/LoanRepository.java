package com.lolc.api.rest.repository;

import com.lolc.api.rest.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findLoansByCustomer_CustomerId(Long customerId);

    List<Loan> findByCreateAtGreaterThanEqualAndCreateAtLessThanOrderByCreateAtAsc(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );
}
