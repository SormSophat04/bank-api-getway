package com.lolc.api.getway.service;

import com.google.auth.oauth2.AccessToken;

import java.io.IOException;

public interface FcmCredentialsClient {

    AccessToken getAccessToken() throws IOException;
}
