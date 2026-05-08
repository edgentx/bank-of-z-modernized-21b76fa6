package com.example.application;

import com.example.domain.ports.SlackMessageValidator;
import com.example.domain.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

@Service
public class SlackNotificationService {

    private final SlackNotificationPort slackNotificationPort;
    private final SlackMessageValidator validator;

    public SlackNotificationService(SlackNotificationPort slackNotificationPort, SlackMessageValidator validator) {
        this.slackNotificationPort = slackNotificationPort;
        this.validator = validator;
    }

    public void sendNotification(String channel, String message) {
        // In a real scenario, we might validate here, or before sending.
        // For S-FB-1, the requirement is to validate the body contains the URL.
        // This service acts as the bridge.
        slackNotificationPort.sendNotification(channel, message);
    }

    public boolean validateContent(String body, String expectedUrl) {
        return validator.validateBodyContainsUrl(body, expectedUrl);
    }
}
