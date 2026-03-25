package com.lolc.api.getway.rest;

import com.lolc.api.getway.dto.request.FcmTokenUpdateRequest;
import com.lolc.api.getway.dto.response.CustomerResponse;
import com.lolc.api.getway.entity.Customer;
import com.lolc.api.getway.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        Customer customerInfo = customerService.createCustomer(customer);
        return ResponseEntity.ok(customerInfo);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        List<CustomerResponse> customers = customerService.getCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long customerId) {
        Customer customer = customerService.getCustomer(customerId);
        return  ResponseEntity.ok(customer);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<Customer> updateCustomer(@RequestBody Customer customer, @PathVariable Long customerId) {
        Customer update = customerService.update(customer, customerId);
        return ResponseEntity.ok(update);
    }

    @PutMapping("/{customerId}/fcm-token")
    public ResponseEntity<?> updateFcmToken(
            @PathVariable Long customerId,
            @Valid @RequestBody FcmTokenUpdateRequest request
    ) {
        customerService.updateFcmToken(customerId, request.fcmToken());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<?> deleteCustomerById(@PathVariable Long customerId) {
        customerService.delete(customerId);
        return ResponseEntity.ok().body("Deleted");
    }
}
