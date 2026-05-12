package com.example.ports;

/**
 * Outbound port for posting a formatted message to Slack.
 */
public interface SlackNotificationPort {
  void sendNotification(String message);
}
