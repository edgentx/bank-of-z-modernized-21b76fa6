package com.example.ports;

/**
 * Port interface for Slack notification integration.
 */
public interface SlackPort {
    String getLastMessageBody();
    void postMessage(String text);
}