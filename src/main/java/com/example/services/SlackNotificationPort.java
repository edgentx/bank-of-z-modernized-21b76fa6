package com.example.services;

/** Port for sending Slack notifications */
public interface SlackNotificationPort {
    void send(String message);
}