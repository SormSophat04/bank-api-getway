package com.lolc.api.getway.impl;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FcmJwtTimeClaimsTest {

    @Test
    void fromUtcInstantShouldUseUnixSecondsAndOneHourWindow() {
        Instant fixedUtcInstant = Instant.parse("2026-03-21T12:34:56Z");

        FcmJwtTimeClaims claims = FcmJwtTimeClaims.fromUtcInstant(fixedUtcInstant);

        assertEquals(fixedUtcInstant.getEpochSecond(), claims.iatSeconds());
        assertEquals(fixedUtcInstant.getEpochSecond() + 3600, claims.expSeconds());
        assertTrue(claims.isValidWindow());
    }

    @Test
    void isValidWindowShouldRejectLifetimeLongerThanOneHour() {
        FcmJwtTimeClaims invalidClaims = new FcmJwtTimeClaims(100L, 3801L);

        assertFalse(invalidClaims.isValidWindow());
    }
}
