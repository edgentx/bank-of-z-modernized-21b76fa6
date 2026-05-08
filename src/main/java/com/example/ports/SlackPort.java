package com.example.ports;

/**
 * Interface for Slack notification operations.
 */
public interface SlackPort {
    void sendNotification(String message);
}