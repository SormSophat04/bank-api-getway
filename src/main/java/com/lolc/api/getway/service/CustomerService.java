package com.lolc.api.getway.service;

import com.lolc.api.getway.dto.response.CustomerResponse;
import com.lolc.api.getway.entity.Customer;

import java.util.List;

public interface CustomerService {
    Customer createCustomer(Customer customer);
    List<CustomerResponse> getCustomers();
    Customer getCustomer(Long customerId);
    Customer update(Customer customer, Long customerId);
    Customer updateFcmToken(Long customerId, String fcmToken);
    void delete(Long customerId);
}
