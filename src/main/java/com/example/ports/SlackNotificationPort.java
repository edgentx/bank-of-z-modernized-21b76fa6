package com.example.ports;

public interface SlackNotificationPort {
    void sendMessage(String channel, String message);
}
