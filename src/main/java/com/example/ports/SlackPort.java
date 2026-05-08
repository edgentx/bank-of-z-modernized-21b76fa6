package com.example.ports;

/** Interface for Slack integration */
public interface SlackPort {
    void sendMessage(String messageBody);
}