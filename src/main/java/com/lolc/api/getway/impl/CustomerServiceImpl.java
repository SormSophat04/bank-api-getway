package com.lolc.api.getway.impl;

import com.lolc.api.getway.entity.Customer;
import com.lolc.api.getway.repository.CustomerRepository;
import com.lolc.api.getway.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    @Override
    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Customer getCustomer(Long customerId) {
        return customerRepository.findById(customerId).get();
    }

    @Override
    public Customer update(Customer customer, Long customerId) {
        Customer customerIds = getCustomer(customerId);
        customerIds.setFirstName(customer.getFirstName());
        customerIds.setLastName(customer.getLastName());
        customerIds.setPhone(customer.getPhone());
        customerIds.setEmail(customer.getEmail());
        customerIds.setNationalId(customer.getNationalId());
        customerIds.setBirthDate(customer.getBirthDate());
        customerIds.setStatus(customer.getStatus());
        return customerRepository.save(customerIds);
    }

    @Override
    public void delete(Long customerId) {
        Customer customer = getCustomer(customerId);
        customerRepository.delete(customer);
    }
}
