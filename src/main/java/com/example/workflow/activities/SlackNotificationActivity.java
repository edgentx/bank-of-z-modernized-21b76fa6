package com.example.workflow.activities;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface SlackNotificationActivity {
    void sendNotification(String channel, String message);
}
