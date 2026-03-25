package com.lolc.api.getway.impl;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.lolc.api.getway.service.FcmCredentialsClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class GoogleFcmCredentialsClient implements FcmCredentialsClient {

    private final GoogleCredentials firebaseCredentials;

    @Override
    public AccessToken getAccessToken() throws IOException {
        firebaseCredentials.refreshIfExpired();

        AccessToken token = firebaseCredentials.getAccessToken();
        if (token != null) {
            return token;
        }

        AccessToken refreshedToken = firebaseCredentials.refreshAccessToken();
        if (refreshedToken == null) {
            throw new IOException("Firebase credentials returned a null access token");
        }
        return refreshedToken;
    }
}
