package com.lolc.api.getway.rest;

import com.lolc.api.getway.dto.request.PushNotificationRequest;
import com.lolc.api.getway.dto.response.PushNotificationResponse;
import com.lolc.api.getway.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/push")
    public ResponseEntity<PushNotificationResponse> sendPushNotification(
            @Valid @RequestBody PushNotificationRequest request
    ) {
        String messageId = notificationService.sendToReceiver(request);
        PushNotificationResponse response = new PushNotificationResponse(messageId, "Push notification sent");
        return ResponseEntity.ok(response);
    }
}
