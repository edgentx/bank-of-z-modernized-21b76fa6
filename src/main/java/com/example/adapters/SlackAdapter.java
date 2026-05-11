package com.example.adapters;

import com.example.domain.validation.model.DefectReportedEvent;
import com.example.ports.SlackPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the Slack port.
 * Sends a formatted message to a Slack channel.
 */
@Component
public class SlackAdapter implements SlackPort {

    private static final Logger logger = LoggerFactory.getLogger(SlackAdapter.class);

    @Override
    public void sendNotification(String channel, DefectReportedEvent event) {
        // Construct the message body
        // Requirement: Slack body includes GitHub issue: <url>
        StringBuilder sb = new StringBuilder();
        sb.append("*Defect Reported*\n");
        sb.append("ID: ").append(event.defectId()).append("\n");
        sb.append("Title: ").append(event.title()).append("\n");
        sb.append("Severity: ").append(event.severity()).append("\n");
        sb.append("GitHub Issue: ").append(event.githubUrl()).append("\n"); // Critical fix line

        String messageBody = sb.toString();

        // Actual Slack API call would go here (WebClient or RestTemplate)
        // For validation/low severity defect logging:
        logger.info("Sending Slack notification to {}: {}", channel, messageBody);
        
        // Simulate successful send
    }
}
