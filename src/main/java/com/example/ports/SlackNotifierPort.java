package com.example.ports;

/**
 * Port interface for sending notifications to Slack.
 */
public interface SlackNotifierPort {
    void notify(String message);
}