package com.example.ports;

/**
 * Port interface for Slack notification operations.
 * Abstracts the specific Slack client implementation (e.g., using Slack SDK or HTTP client).
 */
public interface SlackPort {
    void sendMessage(String messageBody);
}
