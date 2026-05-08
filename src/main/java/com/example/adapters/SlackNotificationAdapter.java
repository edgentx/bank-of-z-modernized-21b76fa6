package com.example.adapters;

import com.example.domain.report_defect.model.ReportDefectCommand;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Real-world adapter for Slack notifications.
 * In a production environment, this would use the Slack Web API to post a message.
 * For the scope of this fix (VW-454), it correctly formats the body including the GitHub URL.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    private final String githubBaseUrl;

    public SlackNotificationAdapter(
            @Value("${vforce360.github.base-url:https://github.com/organization/repo/issues/}") String githubBaseUrl) {
        this.githubBaseUrl = githubBaseUrl;
    }

    @Override
    public String sendDefectNotification(ReportDefectCommand command) {
        // Construct the GitHub URL as per the defect requirements
        String githubUrl = githubBaseUrl + command.defectId();

        // Format the Slack body message
        // Requirements: "Slack body includes GitHub issue: <url>"
        String body = String.format(
            "Defect Reported: %s\nSeverity: %s\nGitHub Issue: %s",
            command.title(),
            command.severity(),
            githubUrl
        );

        // Here we would ostensibly call an external HTTP client (e.g., WebClient)
        // POST https://slack.com/api/chat.postMessage
        // For now, we log and return the formatted body for validation.
        log.info("Sending Slack notification for defect {}: {}", command.defectId(), body);

        return body;
    }
}
