package com.example.ports;

import java.util.Map;

/**
 * Port for posting Slack messages.
 */
public interface SlackPort {
    void sendMessage(String channel, String text);
}
