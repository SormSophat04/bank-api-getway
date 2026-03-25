package com.lolc.api.getway.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record PushNotificationRequest(
        @NotBlank(message = "Receiver account number is required")
        String toAccountNumber,
        @NotBlank(message = "Title is required")
        String title,
        @NotBlank(message = "Body is required")
        String body,
        Map<String, String> data
) {
}
