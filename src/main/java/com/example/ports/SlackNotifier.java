package com.example.ports;

/**
 * Port interface for Slack notification.
 * Following the Hexagonal Architecture pattern.
 */
public interface SlackNotifier {
    void send(String messageBody);
}
