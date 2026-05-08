package com.example.validation.domain.ports;

/**
 * Port interface for sending Slack notifications.
 * Following the Hexagonal Architecture pattern, this defines the contract
 * that the infrastructure layer must fulfill.
 */
public interface SlackNotificationPort {
    void sendMessage(String message);
}