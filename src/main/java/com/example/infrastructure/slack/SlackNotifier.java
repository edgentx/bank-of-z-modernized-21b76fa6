package com.example.infrastructure.slack;

/**
 * Interface for Slack notification operations.
 */
public interface SlackNotifier {
    void send(String message);
}