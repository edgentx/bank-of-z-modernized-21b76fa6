package com.example.ports;

public interface SlackNotifier {
    void send(String channel, String message);
}
