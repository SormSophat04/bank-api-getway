package com.lolc.api.getway.service;

import com.lolc.api.getway.dto.request.LoginRequest;
import com.lolc.api.getway.dto.request.RefreshTokenRequest;
import com.lolc.api.getway.dto.request.RegisterRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> login(LoginRequest loginRequest);
    ResponseEntity<?> register(RegisterRequest registerRequest);
    ResponseEntity<?> refreshToken(RefreshTokenRequest request);
}
