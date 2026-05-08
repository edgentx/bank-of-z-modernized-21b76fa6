package com.example.validation.infrastructure.temporal;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface SlackNotificationActivities {
    void sendSlackNotification(String title, String githubUrl, String severity);
}