package com.example.workflow.activities;

import com.example.ports.SlackNotificationPort;
import io.temporal.activity.ActivityInterface;
import org.springframework.stereotype.Component;

/**
 * Temporal Activity Interface for Slack Notification.
 */
@ActivityInterface
public interface SlackNotificationActivity extends SlackNotificationPort {
    // Inherits methods from SlackNotificationPort
}
