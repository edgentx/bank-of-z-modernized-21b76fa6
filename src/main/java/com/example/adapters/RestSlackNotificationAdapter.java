package com.example.adapters;

import com.example.domain.validation.model.DefectReportedEvent;
import com.example.ports.SlackNotificationPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Real adapter for Slack notifications.
 */
@Service
@Profile("!test")
public class RestSlackNotificationAdapter implements SlackNotificationPort {

    private final RestClient restClient = RestClient.create();
    private final String slackWebhookUrl;

    public RestSlackNotificationAdapter() {
        // In a real scenario, this comes from configuration
        this.slackWebhookUrl = System.getenv("SLACK_WEBHOOK_URL");
    }

    @Override
    public void sendDefectNotification(DefectReportedEvent event) {
        if (slackWebhookUrl == null) return;

        String message = String.format(
            "Defect Reported: %s\nProject: %s\nGitHub Issue: <%s|Link>",
            event.title(), event.projectId(), event.githubUrl()
        );

        // Synchronous call (in a real app, this should be async)
        try {
            restClient.post()
                .uri(slackWebhookUrl)
                .body(message)
                .retrieve();
        } catch (Exception e) {
            // Log error but don't fail the aggregate transaction
            System.err.println("Failed to send Slack notification: " + e.getMessage());
        }
    }
}
