package com.lolc.api.getway.impl;

import com.google.auth.oauth2.AccessToken;
import com.lolc.api.getway.service.FcmAuthService;
import com.lolc.api.getway.service.FcmCredentialsClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class FcmAuthServiceImpl implements FcmAuthService {

    private static final Logger log = LoggerFactory.getLogger(FcmAuthServiceImpl.class);

    private final FcmCredentialsClient fcmCredentialsClient;
    private final Clock utcClock;

    @Override
    public void assertReadyForSend() {
        Instant utcNow = Instant.now(utcClock);
        FcmJwtTimeClaims jwtTimeClaims = FcmJwtTimeClaims.fromUtcInstant(utcNow);

        if (!jwtTimeClaims.isValidWindow()) {
            throw new IllegalStateException(
                    "Invalid FCM JWT time window generated: iat=" + jwtTimeClaims.iatSeconds()
                            + ", exp=" + jwtTimeClaims.expSeconds()
                            + ". exp must be <= iat + 3600 seconds."
            );
        }

        log.debug(
                "FCM auth timing check: utcNow={}, tokenIatSec={}, tokenExpSec={}",
                utcNow,
                jwtTimeClaims.iatSeconds(),
                jwtTimeClaims.expSeconds()
        );

        try {
            AccessToken accessToken = fcmCredentialsClient.getAccessToken();
            Instant tokenExpUtc = accessToken.getExpirationTime() == null
                    ? null
                    : accessToken.getExpirationTime().toInstant();

            log.debug("FCM access token expiration UTC: {}", tokenExpUtc);

            if (tokenExpUtc != null && !tokenExpUtc.isAfter(utcNow.minusSeconds(1))) {
                throw new IllegalStateException(
                        "FCM access token is already expired. utcNow=" + utcNow + ", tokenExpUtc=" + tokenExpUtc
                );
            }
        } catch (IOException ex) {
            throw mapAuthException(ex, utcNow, jwtTimeClaims);
        }
    }

    private IllegalStateException mapAuthException(IOException ex, Instant utcNow, FcmJwtTimeClaims jwtTimeClaims) {
        String message = ex.getMessage() == null ? "" : ex.getMessage();
        String lowerMessage = message.toLowerCase();

        if (lowerMessage.contains("invalid_grant")
                && (lowerMessage.contains("reasonable timeframe")
                || lowerMessage.contains("iat")
                || lowerMessage.contains("exp"))) {
            return new IllegalStateException(
                    "FCM authentication failed due to possible server clock skew. "
                            + "utcNow=" + utcNow
                            + ", tokenIatSec=" + jwtTimeClaims.iatSeconds()
                            + ", tokenExpSec=" + jwtTimeClaims.expSeconds()
                            + ". Ensure host time is synchronized via NTP and JWT exp <= iat + 3600 seconds.",
                    ex
            );
        }

        return new IllegalStateException("Failed to refresh FCM access token: " + message, ex);
    }
}
