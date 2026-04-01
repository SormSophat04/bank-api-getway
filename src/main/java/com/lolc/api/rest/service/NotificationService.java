package com.lolc.api.rest.service;

import com.lolc.api.rest.dto.request.PushNotificationRequest;

public interface NotificationService {

    String sendToReceiver(PushNotificationRequest request);
}
