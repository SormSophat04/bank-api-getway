package com.lolc.api.getway.service;

import com.lolc.api.getway.entity.Customer;

import java.util.List;

public interface CustomerService {
    Customer createCustomer(Customer customer);
    List<Customer> getCustomers();
    Customer getCustomer(Long customerId);
    Customer update(Customer customer, Long customerId);
    void delete(Long customerId);
}
