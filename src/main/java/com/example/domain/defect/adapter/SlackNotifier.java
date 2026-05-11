package com.example.domain.defect.adapter;

/**
 * Port interface for sending Slack notifications.
 */
public interface SlackNotifier {
    void notify(String title, String url);
}