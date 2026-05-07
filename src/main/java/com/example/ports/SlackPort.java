package com.example.ports;

import java.util.Map;

public interface SlackPort {
    void sendMessage(String channel, Map<String, String> message);
}