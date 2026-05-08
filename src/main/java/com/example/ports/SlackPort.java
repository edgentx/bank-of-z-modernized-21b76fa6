package com.example.ports;

public interface SlackPort {
    void postMessage(String channel, String text);
}
