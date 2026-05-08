package com.example.adapters;

import com.example.domain.defect.model.DefectReportedEvent;
import com.example.ports.NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the NotificationPort.
 * In a production environment, this would use the Slack WebClient to post messages.
 */
@Component
public class SlackNotificationAdapter implements NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    // In a real Spring Boot app, you would inject SlackClient here
    // private final SlackClient slackClient;

    public SlackNotificationAdapter() {
        // Dependency injection would happen here
    }

    @Override
    public void sendDefectAlert(DefectReportedEvent event) {
        // Construct the Slack message body
        // Requirement: "Slack body includes GitHub issue: <url>"
        String messageBody = String.format(
                "Defect Reported: %s | GitHub issue: %s",
                event.title(),
                event.githubUrl()
        );

        // Simulate sending to Slack API
        log.info("[Slack Outbound] Sending notification: {}", messageBody);

        // Real implementation would be:
        // slackClient.postMessage(chatId, messageBody);
    }
}
