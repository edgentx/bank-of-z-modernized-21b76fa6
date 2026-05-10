package com.example.ports;

public interface SlackNotificationPort {
    void notify(String channel, String message);
}
