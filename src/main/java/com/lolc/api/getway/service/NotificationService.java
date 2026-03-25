package com.lolc.api.getway.service;

import com.lolc.api.getway.dto.request.PushNotificationRequest;

public interface NotificationService {

    String sendToReceiver(PushNotificationRequest request);
}
