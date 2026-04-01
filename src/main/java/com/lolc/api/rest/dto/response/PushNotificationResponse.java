package com.lolc.api.rest.dto.response;

public record PushNotificationResponse(
        String messageId,
        String message
) {
}
