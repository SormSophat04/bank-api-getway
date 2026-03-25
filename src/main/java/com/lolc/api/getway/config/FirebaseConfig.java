package com.lolc.api.getway.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.time.Clock;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
public class FirebaseConfig {

    private final ResourceLoader resourceLoader;

    @Value("${firebase.credentials.path:classpath:serviceAccountKey.json}")
    private String credentialsPath;

    public FirebaseConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public Clock utcClock() {
        return Clock.systemUTC();
    }

    @Bean
    public GoogleCredentials firebaseCredentials() throws IOException {
        Resource resource = resourceLoader.getResource(credentialsPath);
        if (!resource.exists()) {
            throw new IllegalStateException("Firebase service account file not found: " + credentialsPath);
        }

        try (InputStream serviceAccount = resource.getInputStream()) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
            if (credentials.createScopedRequired()) {
                credentials = credentials.createScoped(List.of("https://www.googleapis.com/auth/firebase.messaging"));
            }
            return credentials;
        }
    }

    @Bean
    public FirebaseApp firebaseApp(GoogleCredentials firebaseCredentials) {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getApps().getFirst();
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(firebaseCredentials)
                .build();
        return FirebaseApp.initializeApp(options);
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
