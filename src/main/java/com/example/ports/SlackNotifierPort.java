package com.example.ports;

/**
 * Port interface for Slack Notifications.
 */
public interface SlackNotifierPort {
    void notify(String message);
}