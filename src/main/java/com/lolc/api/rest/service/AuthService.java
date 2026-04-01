package com.lolc.api.rest.service;

import com.lolc.api.rest.dto.request.LoginRequest;
import com.lolc.api.rest.dto.request.RefreshTokenRequest;
import com.lolc.api.rest.dto.request.RegisterRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> login(LoginRequest loginRequest);
    ResponseEntity<?> register(RegisterRequest registerRequest);
    ResponseEntity<?> refreshToken(RefreshTokenRequest request);
}
