package com.lolc.api.getway.impl;

import java.time.Instant;

record FcmJwtTimeClaims(long iatSeconds, long expSeconds) {

    static final long MAX_TOKEN_LIFETIME_SECONDS = 3600L;

    static FcmJwtTimeClaims fromUtcInstant(Instant nowUtc) {
        long iatSeconds = nowUtc.getEpochSecond();
        long expSeconds = iatSeconds + MAX_TOKEN_LIFETIME_SECONDS;
        return new FcmJwtTimeClaims(iatSeconds, expSeconds);
    }

    boolean isValidWindow() {
        return iatSeconds > 0
                && expSeconds > iatSeconds
                && (expSeconds - iatSeconds) <= MAX_TOKEN_LIFETIME_SECONDS;
    }
}
