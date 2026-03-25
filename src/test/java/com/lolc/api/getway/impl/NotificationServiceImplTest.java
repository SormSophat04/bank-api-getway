package com.lolc.api.getway.impl;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.lolc.api.getway.dto.request.PushNotificationRequest;
import com.lolc.api.getway.entity.Account;
import com.lolc.api.getway.entity.Customer;
import com.lolc.api.getway.repository.AccountRepository;
import com.lolc.api.getway.service.FcmAuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @Mock
    private FcmAuthService fcmAuthService;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void sendToReceiverShouldRefreshAuthAndSendPushMessage() throws Exception {
        PushNotificationRequest request = new PushNotificationRequest(
                "ACC-001",
                "Transfer Alert",
                "You received LKR 5000",
                Map.of("type", "credit")
        );

        Customer customer = new Customer();
        customer.setFcmToken("fcm-token-123");

        Account account = new Account();
        account.setCustomer(customer);

        when(accountRepository.findByAccountNumber("ACC-001")).thenReturn(Optional.of(account));
        when(firebaseMessaging.send(any(Message.class))).thenReturn("projects/banking/messages/abc123");

        String messageId = notificationService.sendToReceiver(request);

        assertEquals("projects/banking/messages/abc123", messageId);
        verify(fcmAuthService).assertReadyForSend();

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(firebaseMessaging).send(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        org.junit.jupiter.api.Assertions.assertNotNull(sentMessage);
    }
}
