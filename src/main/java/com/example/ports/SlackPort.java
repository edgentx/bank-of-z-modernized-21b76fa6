package com.example.ports;

/** Port for Slack integration */
public interface SlackPort {
    /** Sends a notification containing the link */
    void sendNotification(String url);
}