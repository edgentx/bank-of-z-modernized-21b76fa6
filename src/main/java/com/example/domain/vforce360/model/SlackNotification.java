package com.example.domain.vforce360.model;

public record SlackNotification(
        String channel,
        String body
) {
    public SlackNotification {
        if (body == null) throw new IllegalArgumentException("body cannot be null");
    }
}