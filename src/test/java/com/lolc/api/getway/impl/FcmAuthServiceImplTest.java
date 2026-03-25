package com.lolc.api.getway.impl;

import com.google.auth.oauth2.AccessToken;
import com.lolc.api.getway.service.FcmCredentialsClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FcmAuthServiceImplTest {

    @Mock
    private FcmCredentialsClient fcmCredentialsClient;

    @Test
    void assertReadyForSendShouldPassWithValidToken() throws IOException {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-03-21T10:00:00Z"), ZoneOffset.UTC);
        FcmAuthServiceImpl fcmAuthService = new FcmAuthServiceImpl(fcmCredentialsClient, fixedClock);

        AccessToken accessToken = new AccessToken("token", Date.from(Instant.parse("2026-03-21T10:30:00Z")));
        when(fcmCredentialsClient.getAccessToken()).thenReturn(accessToken);

        assertDoesNotThrow(fcmAuthService::assertReadyForSend);
    }

    @Test
    void assertReadyForSendShouldFailFastOnInvalidGrantTimeframeError() throws IOException {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-03-21T10:00:00Z"), ZoneOffset.UTC);
        FcmAuthServiceImpl fcmAuthService = new FcmAuthServiceImpl(fcmCredentialsClient, fixedClock);

        IOException invalidGrant = new IOException(
                "400 Bad Request {\"error\":\"invalid_grant\",\"error_description\":\"Invalid JWT: Token must be a short-lived token (60 minutes) and in a reasonable timeframe. Check your iat and exp values in the JWT claim.\"}"
        );
        when(fcmCredentialsClient.getAccessToken()).thenThrow(invalidGrant);

        IllegalStateException exception = assertThrows(IllegalStateException.class, fcmAuthService::assertReadyForSend);
        String message = exception.getMessage();
        org.junit.jupiter.api.Assertions.assertTrue(message.contains("possible server clock skew"));
        org.junit.jupiter.api.Assertions.assertTrue(message.contains("tokenIatSec"));
        org.junit.jupiter.api.Assertions.assertTrue(message.contains("tokenExpSec"));
    }
}
