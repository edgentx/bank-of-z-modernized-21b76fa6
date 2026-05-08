package com.example.adapters.slack;

/**
 * Port interface for Slack notifications.
 * Implementations will use the Slack WebClient (real) or Mocks (test).
 */
public interface SlackPort {
    void sendMessage(String messageBody);
}
