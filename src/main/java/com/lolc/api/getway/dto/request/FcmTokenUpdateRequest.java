package com.lolc.api.getway.dto.request;

import jakarta.validation.constraints.NotBlank;

public record FcmTokenUpdateRequest(
        @NotBlank(message = "FCM token is required")
        String fcmToken
) {
}
