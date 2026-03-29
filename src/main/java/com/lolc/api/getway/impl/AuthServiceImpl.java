package com.lolc.api.getway.impl;

import com.lolc.api.getway.security.JwtService;
import com.lolc.api.getway.dto.request.LoginRequest;
import com.lolc.api.getway.dto.request.RefreshTokenRequest;
import com.lolc.api.getway.dto.request.RegisterRequest;
import com.lolc.api.getway.entity.Customer;
import com.lolc.api.getway.entity.User;
import com.lolc.api.getway.exception.ConflictException;
import com.lolc.api.getway.repository.AuthRepository;
import com.lolc.api.getway.repository.CustomerRepository;
import com.lolc.api.getway.config.PasswordConfig;
import com.lolc.api.getway.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthRepository authRepository;
    private final CustomerRepository customerRepository;
    private final PasswordConfig passwordConfig;

    @Override
    public ResponseEntity<?> login(LoginRequest request) {
        if (request == null || isBlank(request.phoneNumber()) || isBlank(request.password())) {
            throw new IllegalArgumentException("Phone number and password are required");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.phoneNumber().trim(),
                        request.password()
                )
        );

        User user = getAuthenticatedUser(authentication.getName());
        String token = jwtService.generateToken(authentication.getName(), buildTokenClaims(user));
        String refreshToken = jwtService.generateRefreshToken(authentication.getName());
        
        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "tokenType", "Bearer",
                "token", token,
                "refreshToken", refreshToken
        ));
    }

    @Override
    public ResponseEntity<?> register(RegisterRequest request) {
        if (request == null || isBlank(request.phoneNumber()) || isBlank(request.email()) || isBlank(request.password())) {
            throw new IllegalArgumentException("Phone number, email and password are required");
        }

        String phoneNumber = normalizePhoneNumber(request.phoneNumber());
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        if (authRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new ConflictException("Phone number already exists");
        }

        if (authRepository.findByEmail(email).isPresent()) {
            throw new ConflictException("Email already exists");
        }

        User user = new User();
        user.setPassword(passwordConfig.getPasswordEncoder().encode(request.password()));
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setRole("USER");
        user.setStatus("ACTIVE");
        authRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        phoneNumber,
                        request.password()
                )
        );

        User authenticatedUser = getAuthenticatedUser(authentication.getName());
        String token = jwtService.generateToken(authentication.getName(), buildTokenClaims(authenticatedUser));
        String refreshToken = jwtService.generateRefreshToken(authentication.getName());

        return ResponseEntity.ok(Map.of(
                "message", "Registration successful",
                "tokenType", "Bearer",
                "token", token,
                "refreshToken", refreshToken
        ));
    }

    @Override
    public ResponseEntity<?> refreshToken(RefreshTokenRequest request) {
        if (request == null || isBlank(request.getRefreshToken())) {
            throw new IllegalArgumentException("Refresh token is required");
        }

        String refreshToken = request.getRefreshToken();
        String principal;
        try {
            principal = jwtService.extractUsername(refreshToken);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        if (principal != null) {
            User user = getAuthenticatedUser(principal);
            if (jwtService.isTokenValid(refreshToken, principal)) {
                String accessToken = jwtService.generateToken(principal, buildTokenClaims(user));
                return ResponseEntity.ok(Map.of(
                        "message", "Token refreshed successfully",
                        "tokenType", "Bearer",
                        "token", accessToken,
                        "refreshToken", refreshToken
                ));
            }
        }
        throw new IllegalArgumentException("Invalid or expired refresh token");
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String normalizePhoneNumber(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private User getAuthenticatedUser(String principal) {
        return authRepository.findByPhoneNumber(principal)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));
    }

    private Map<String, Object> buildTokenClaims(User user) {
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("user", buildUserClaims(user));

        customerRepository.findByUser_UserId(user.getUserId())
                .map(this::buildCustomerClaims)
                .ifPresent(customer -> claims.put("customer", customer));

        return claims;
    }

    private Map<String, Object> buildUserClaims(User user) {
        Map<String, Object> userClaims = new LinkedHashMap<>();
        userClaims.put("userId", user.getUserId());
        userClaims.put("phoneNumber", user.getPhoneNumber());
        userClaims.put("email", user.getEmail());
        userClaims.put("role", user.getRole());
        userClaims.put("status", user.getStatus());
        return userClaims;
    }

    private Map<String, Object> buildCustomerClaims(Customer customer) {
        Map<String, Object> customerClaims = new LinkedHashMap<>();
        customerClaims.put("customerId", customer.getCustomerId());
        customerClaims.put("firstName", customer.getFirstName());
        customerClaims.put("lastName", customer.getLastName());
        customerClaims.put("phone", customer.getPhone());
        customerClaims.put("email", customer.getEmail());
        customerClaims.put("nationalId", customer.getNationalId());
        customerClaims.put("birthDate", customer.getBirthDate() == null ? null : customer.getBirthDate().toString());
        customerClaims.put("status", customer.getStatus());
        return customerClaims;
    }
}
