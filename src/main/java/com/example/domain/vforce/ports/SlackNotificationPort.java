package com.example.domain.vforce.ports;

public interface SlackNotificationPort {
    void postMessage(String channel, String text);
}
