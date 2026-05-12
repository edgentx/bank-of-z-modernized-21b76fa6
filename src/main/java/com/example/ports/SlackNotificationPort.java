package com.example.ports;

public interface SlackNotificationPort {
  /** Post a message to a Slack channel. Body must already include the GitHub issue URL (VW-454). */
  void postToChannel(String channel, String body);
}
