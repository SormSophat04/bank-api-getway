package com.lolc.api.getway.impl;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.lolc.api.getway.dto.request.PushNotificationRequest;
import com.lolc.api.getway.entity.Account;
import com.lolc.api.getway.exception.ResourceNotFoundException;
import com.lolc.api.getway.repository.AccountRepository;
import com.lolc.api.getway.service.FcmAuthService;
import com.lolc.api.getway.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final AccountRepository accountRepository;
    private final FirebaseMessaging firebaseMessaging;
    private final FcmAuthService fcmAuthService;

    @Override
    @Transactional
    public String sendToReceiver(PushNotificationRequest request) {
        Account receiverAccount = accountRepository.findByAccountNumber(request.toAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Receiver account not found: " + request.toAccountNumber()
                ));

        if (receiverAccount.getCustomer() == null) {
            throw new ResourceNotFoundException("Receiver customer profile not found");
        }

        String fcmToken = receiverAccount.getCustomer().getFcmToken();
        if (!StringUtils.hasText(fcmToken)) {
            throw new IllegalArgumentException("Receiver has no registered FCM token");
        }

        Message.Builder messageBuilder = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                        .setTitle(request.title())
                        .setBody(request.body())
                        .build());

        Map<String, String> validData = sanitizeData(request.data());
        if (!validData.isEmpty()) {
            messageBuilder.putAllData(validData);
        }

        try {
            fcmAuthService.assertReadyForSend();
            return firebaseMessaging.send(messageBuilder.build());
        } catch (FirebaseMessagingException ex) {
            throw new IllegalStateException("Failed to send notification: " + ex.getMessage(), ex);
        }
    }

    private Map<String, String> sanitizeData(Map<String, String> data) {
        if (data == null || data.isEmpty()) {
            return Map.of();
        }

        Map<String, String> sanitized = new LinkedHashMap<>();
        data.forEach((key, value) -> {
            if (StringUtils.hasText(key) && value != null) {
                sanitized.put(key, value);
            }
        });
        return sanitized;
    }
}
