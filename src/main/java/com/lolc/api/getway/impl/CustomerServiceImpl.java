package com.lolc.api.getway.impl;

import com.lolc.api.getway.dto.response.CustomerResponse;
import com.lolc.api.getway.entity.Account;
import com.lolc.api.getway.entity.Customer;
import com.lolc.api.getway.entity.User;
import com.lolc.api.getway.enums.Currency;
import com.lolc.api.getway.enums.Status;
import com.lolc.api.getway.exception.ConflictException;
import com.lolc.api.getway.exception.ResourceNotFoundException;
import com.lolc.api.getway.mapper.CustomerMapper;
import com.lolc.api.getway.repository.AccountRepository;
import com.lolc.api.getway.repository.AuthRepository;
import com.lolc.api.getway.repository.CustomerRepository;
import com.lolc.api.getway.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final AuthRepository authRepository;
    private final AccountRepository accountRepository;

    @Override
    public Customer createCustomer(Customer customer) {
        User authenticatedUser = getAuthenticatedUser();
        if (customerRepository.existsByUser_UserId(authenticatedUser.getUserId())) {
            throw new ConflictException("Authenticated user already has a customer profile");
        }
        customer.setUser(authenticatedUser);
        applyContactFromUser(customer, authenticatedUser);
        Customer savedCustomer = customerRepository.save(customer);
        createDefaultAccounts(savedCustomer);
        return savedCustomer;
    }

    private void createDefaultAccounts(Customer customer) {
        createAndSaveAccount(customer, Currency.USD);
        createAndSaveAccount(customer, Currency.KHR);
    }

    private void createAndSaveAccount(Customer customer, Currency currency) {
        Account account = new Account();
        account.setCustomer(customer);
        account.setAccountType("SAVING");
        account.setAccountNumber(generateUniqueAccountNumber());
        account.setBalance(BigDecimal.valueOf(1000));
        account.setCurrency(currency);
        account.setStatus(String.valueOf(Status.ACTIVE));
        accountRepository.save(account);
    }

    private String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            accountNumber = generateRandomAccountNumber();
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

    private String generateRandomAccountNumber() {
        Random random = new Random();
        String part1 = String.format("%04d", random.nextInt(10000));
        String part2 = String.format("%04d", random.nextInt(10000));
        String part3 = String.format("%04d", random.nextInt(10000));
        return part1 + part2 + part3;
    }

    @Override
    public List<CustomerResponse> getCustomers() {
        return customerRepository.findAll().stream().map(customerMapper::toResponse).toList();
    }

    @Override
    public Customer getCustomer(Long customerId) {
        return customerRepository.findById(customerId).orElseThrow(
                () -> new ResourceNotFoundException("Customer not found " + customerId)
        );
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
        applyContactFromAuthenticatedUser(customerIds);
        return customerRepository.save(customerIds);
    }

    @Override
    public Customer updateFcmToken(Long customerId, String fcmToken) {
        Customer customer = getCustomer(customerId);
        customer.setFcmToken(fcmToken);
        return customerRepository.save(customer);
    }

    @Override
    public void delete(Long customerId) {
        Customer customer = getCustomer(customerId);
        customerRepository.delete(customer);
    }

    private void applyContactFromAuthenticatedUser(Customer customer) {
        User user = getAuthenticatedUserOrNull();
        if (user == null) {
            return;
        }

        applyContactFromUser(customer, user);
    }

    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String username = authentication.getName();
        return "anonymousUser".equals(username) ? null : username;
    }

    private User getAuthenticatedUser() {
        User user = getAuthenticatedUserOrNull();
        if (user == null) {
            throw new ResourceNotFoundException("Authenticated user not found");
        }
        return user;
    }

    private User getAuthenticatedUserOrNull() {
        String principal = getAuthenticatedUsername();
        if (principal == null) {
            return null;
        }
        return authRepository.findByPhoneNumber(principal)
                .orElse(null);
    }

    private void applyContactFromUser(Customer customer, User user) {
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            customer.setEmail(user.getEmail());
        }
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isBlank()) {
            customer.setPhone(user.getPhoneNumber());
        }
        if (user.getStatus() != null && !user.getStatus().isBlank()){
            customer.setStatus(String.valueOf(Status.ACTIVE));
        }
    }
}
