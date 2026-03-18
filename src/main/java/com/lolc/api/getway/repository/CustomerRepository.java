package com.lolc.api.getway.repository;

import com.lolc.api.getway.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
