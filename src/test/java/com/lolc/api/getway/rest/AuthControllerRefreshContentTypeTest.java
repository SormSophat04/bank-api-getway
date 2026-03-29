package com.lolc.api.getway.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolc.api.getway.dto.request.RefreshTokenRequest;
import com.lolc.api.getway.exception.GlobalExceptionHandler;
import com.lolc.api.getway.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerRefreshContentTypeTest {

    @Mock
    private AuthService authService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authService, new ObjectMapper()))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void refreshShouldAcceptTextPlainBody() throws Exception {
        doReturn(ResponseEntity.ok(Map.of("message", "ok")))
                .when(authService)
                .refreshToken(any());

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("refresh-token-value"))
                .andExpect(status().isOk());

        ArgumentCaptor<RefreshTokenRequest> requestCaptor = ArgumentCaptor.forClass(RefreshTokenRequest.class);
        verify(authService).refreshToken(requestCaptor.capture());
        assertEquals("refresh-token-value", requestCaptor.getValue().getRefreshToken());
    }

    @Test
    void refreshShouldAcceptJsonBody() throws Exception {
        doReturn(ResponseEntity.ok(Map.of("message", "ok")))
                .when(authService)
                .refreshToken(any());

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"json-refresh-token\"}"))
                .andExpect(status().isOk());

        ArgumentCaptor<RefreshTokenRequest> requestCaptor = ArgumentCaptor.forClass(RefreshTokenRequest.class);
        verify(authService).refreshToken(requestCaptor.capture());
        assertEquals("json-refresh-token", requestCaptor.getValue().getRefreshToken());
    }

    @Test
    void refreshShouldAcceptJsonStringBody() throws Exception {
        doReturn(ResponseEntity.ok(Map.of("message", "ok")))
                .when(authService)
                .refreshToken(any());

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"json-string-token\""))
                .andExpect(status().isOk());

        ArgumentCaptor<RefreshTokenRequest> requestCaptor = ArgumentCaptor.forClass(RefreshTokenRequest.class);
        verify(authService).refreshToken(requestCaptor.capture());
        assertEquals("json-string-token", requestCaptor.getValue().getRefreshToken());
    }

    @Test
    void refreshShouldAcceptXmlBodyAsRawToken() throws Exception {
        doReturn(ResponseEntity.ok(Map.of("message", "ok")))
                .when(authService)
                .refreshToken(any());

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_XML)
                        .content("<refreshToken>token</refreshToken>"))
                .andExpect(status().isOk());

        ArgumentCaptor<RefreshTokenRequest> requestCaptor = ArgumentCaptor.forClass(RefreshTokenRequest.class);
        verify(authService).refreshToken(requestCaptor.capture());
        assertEquals("<refreshToken>token</refreshToken>", requestCaptor.getValue().getRefreshToken());
    }

    @Test
    void refreshShouldAcceptQuotedTextPlainToken() throws Exception {
        doReturn(ResponseEntity.ok(Map.of("message", "ok")))
                .when(authService)
                .refreshToken(any());

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("\"quoted-refresh-token\""))
                .andExpect(status().isOk());

        ArgumentCaptor<RefreshTokenRequest> requestCaptor = ArgumentCaptor.forClass(RefreshTokenRequest.class);
        verify(authService).refreshToken(requestCaptor.capture());
        assertEquals("quoted-refresh-token", requestCaptor.getValue().getRefreshToken());
    }

    @Test
    void refreshShouldAcceptJsonPayloadInTextPlainBody() throws Exception {
        doReturn(ResponseEntity.ok(Map.of("message", "ok")))
                .when(authService)
                .refreshToken(any());

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("{\"refreshToken\":\"token-from-json-text\"}"))
                .andExpect(status().isOk());

        ArgumentCaptor<RefreshTokenRequest> requestCaptor = ArgumentCaptor.forClass(RefreshTokenRequest.class);
        verify(authService).refreshToken(requestCaptor.capture());
        assertEquals("token-from-json-text", requestCaptor.getValue().getRefreshToken());
    }

    @Test
    void refreshShouldAcceptFormUrlEncodedBody() throws Exception {
        doReturn(ResponseEntity.ok(Map.of("message", "ok")))
                .when(authService)
                .refreshToken(any());

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("refreshToken=form-body-token"))
                .andExpect(status().isOk());

        ArgumentCaptor<RefreshTokenRequest> requestCaptor = ArgumentCaptor.forClass(RefreshTokenRequest.class);
        verify(authService).refreshToken(requestCaptor.capture());
        assertEquals("form-body-token", requestCaptor.getValue().getRefreshToken());
    }

    @Test
    void refreshShouldAcceptRefreshTokenRequestParam() throws Exception {
        doReturn(ResponseEntity.ok(Map.of("message", "ok")))
                .when(authService)
                .refreshToken(any());

        mockMvc.perform(post("/api/auth/refresh")
                        .param("refreshToken", "token-from-param"))
                .andExpect(status().isOk());

        ArgumentCaptor<RefreshTokenRequest> requestCaptor = ArgumentCaptor.forClass(RefreshTokenRequest.class);
        verify(authService).refreshToken(requestCaptor.capture());
        assertEquals("token-from-param", requestCaptor.getValue().getRefreshToken());
    }
}
