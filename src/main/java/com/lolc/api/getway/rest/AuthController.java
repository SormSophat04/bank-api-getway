package com.lolc.api.getway.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolc.api.getway.dto.request.LoginRequest;
import com.lolc.api.getway.dto.request.RefreshTokenRequest;
import com.lolc.api.getway.dto.request.RegisterRequest;
import com.lolc.api.getway.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final ObjectMapper objectMapper;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @RequestBody(required = false) String refreshToken,
            @RequestParam(value = "refreshToken", required = false) String refreshTokenParam
    ) {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken(extractRefreshToken(refreshToken, refreshTokenParam))
                .build();
        return authService.refreshToken(request);
    }

    private String extractRefreshToken(String payload, String refreshTokenParam) {
        if (refreshTokenParam != null && !refreshTokenParam.isBlank()) {
            return refreshTokenParam.trim();
        }

        if (payload == null) {
            return null;
        }

        String normalized = payload.trim();
        if (normalized.isEmpty()) {
            return normalized;
        }

        if (normalized.startsWith("{") && normalized.endsWith("}")) {
            try {
                RefreshTokenRequest parsed = objectMapper.readValue(normalized, RefreshTokenRequest.class);
                return parsed == null ? null : parsed.getRefreshToken();
            } catch (Exception ignored) {
                // Fall back to raw token parsing.
            }
        }

        if (normalized.regionMatches(true, 0, "refreshToken=", 0, "refreshToken=".length())) {
            String encodedValue = normalized.substring("refreshToken=".length());
            return URLDecoder.decode(encodedValue, StandardCharsets.UTF_8).trim();
        }

        if (normalized.length() >= 2 && normalized.startsWith("\"") && normalized.endsWith("\"")) {
            normalized = normalized.substring(1, normalized.length() - 1).trim();
        }

        if (normalized.regionMatches(true, 0, "Bearer ", 0, "Bearer ".length())) {
            normalized = normalized.substring("Bearer ".length()).trim();
        }

        return normalized;
    }
}
