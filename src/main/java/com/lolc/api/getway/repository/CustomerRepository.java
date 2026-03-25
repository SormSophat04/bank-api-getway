package com.lolc.api.getway.repository;

import com.lolc.api.getway.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByUser_UserId(Long userId);

    Optional<Customer> findByUser_UserId(Long userId);
}
