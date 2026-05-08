package com.example.domain.notification.model;

import com.example.domain.shared.Command;

public record PublishNotificationCmd(
    String notificationId,
    String slackBody
) implements Command {}
