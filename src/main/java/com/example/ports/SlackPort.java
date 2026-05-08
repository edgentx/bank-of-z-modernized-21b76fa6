package com.example.ports;

public interface SlackPort {
    void sendMessage(String channel, String body);
}