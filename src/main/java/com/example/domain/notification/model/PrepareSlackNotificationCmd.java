package com.example.domain.notification.model;

import com.example.domain.shared.Command;

public record PrepareSlackNotificationCmd(
    String notificationId,
    String channel,
    String title,
    String githubIssueUrl
) implements Command {}