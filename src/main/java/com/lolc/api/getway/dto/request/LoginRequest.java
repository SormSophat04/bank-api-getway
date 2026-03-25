package com.lolc.api.getway.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Phone number is required")
        @Size(min = 6, max = 20, message = "Phone number must be between 6 and 20 characters")
        String phoneNumber,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password
) { }
