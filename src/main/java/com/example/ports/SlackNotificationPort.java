package com.example.ports;

public interface SlackNotificationPort {
    void sendNotification(String messageBody);
}
