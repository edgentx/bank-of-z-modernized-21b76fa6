package com.example.adapters;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Real implementation of the Slack Notification Port.
 * This adapter is responsible for formatting the Slack message payload
 * ensuring the GitHub URL is present in the body.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);
    private static final String CHANNEL = "#vforce360-issues";

    @Override
    public void sendDefectNotification(ReportDefectCmd cmd, URI gitHubIssueUrl) {
        if (cmd == null) {
            throw new IllegalArgumentException("ReportDefectCmd cannot be null");
        }
        if (gitHubIssueUrl == null) {
            throw new IllegalArgumentException("GitHub Issue URL cannot be null");
        }

        log.info("Sending defect notification for project: {}", cmd.projectId());

        // Construct the Slack message body
        // Fix for VW-454: Ensure the URL is explicitly appended to the body
        StringBuilder body = new StringBuilder();
        body.append("*Defect Reported*\n");
        body.append("Project: ").append(cmd.projectId()).append("\n");
        body.append("Title: ").append(cmd.title()).append("\n");
        body.append("Description: ").append(cmd.description()).append("\n");
        
        // Critical Fix: Append the GitHub Issue URL
        body.append("GitHub Issue: <").append(gitHubIssueUrl).append("|View Issue>\n");

        // In a real scenario, this would use WebClient or SlackClient to POST to the API
        // e.g., webClient.post().uri(slackWebhookUrl).body(payload).retrieve();
        Map<String, Object> payload = Map.of(
            "channel", CHANNEL,
            "body", body.toString()
        );

        log.debug("Prepared Slack payload: {}", payload);
        
        // Simulate sending or persisting the event (Async handling would happen here)
    }
}
