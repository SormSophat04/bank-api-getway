package com.lolc.api.getway.repository;

import com.lolc.api.getway.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}
