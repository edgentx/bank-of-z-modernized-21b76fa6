package com.example.domain.notification.model;

import com.example.domain.shared.Command;

public record SendNotificationCmd(
    String notificationId,
    String channel,
    String target,
    String formattedBody
) implements Command {}
