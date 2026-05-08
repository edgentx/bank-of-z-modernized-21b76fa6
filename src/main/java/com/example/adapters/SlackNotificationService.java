package com.example.adapters;

import com.example.ports.SlackPort;
import org.springframework.stereotype.Service;

/**
 * Adapter implementation for SlackPort.
 * Implementation uses OkHttp but relies on interface for mocking.
 */
@Service
public class SlackNotificationService implements SlackPort {

    // Implementation details hidden from domain
    // This class is primarily for compilation purposes in the test phase.

    @Override
    public void sendNotification(String webhookUrl, String jsonPayload) {
        // Actual implementation would use OkHttpClient here.
        // But for TDD red phase, we don't need to implement it,
        // just ensure the project structure compiles.
        throw new UnsupportedOperationException("Not implemented in TDD Red phase");
    }
}
