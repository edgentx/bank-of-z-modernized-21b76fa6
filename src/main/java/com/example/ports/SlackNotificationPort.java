package com.example.ports;

public interface SlackNotificationPort {
    void sendNotification(String channel, String message);
}
