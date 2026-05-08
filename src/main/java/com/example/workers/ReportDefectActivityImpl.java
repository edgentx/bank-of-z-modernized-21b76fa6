package com.example.workers;

import io.temporal.activity.Activity;
import com.example.ports.SlackPort;
import org.springframework.stereotype.Component;

/**
 * Implementation of the ReportDefectActivity.
 * Uses the SlackPort (Adapter pattern) to perform the actual notification.
 */
@Component
public class ReportDefectActivityImpl implements ReportDefectActivity {

    private final SlackPort slackPort;

    public ReportDefectActivityImpl(SlackPort slackPort) {
        this.slackPort = slackPort;
    }

    @Override
    public String sendToSlack(String title, String description, String githubUrl) {
        // Log heartbeats if this were a long-running operation, but Slack is fast.
        // Activity.heartbeat("Processing Slack notification...");

        // Construct the Slack body as per VW-454 expectations
        // Format: "Slack body includes GitHub issue: <url>"
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Defect Reported: ").append(title != null ? title : "Unknown").append("\n");
        messageBuilder.append("Description: ").append(description != null ? description : "N/A").append("\n");
        
        // The specific fix for VW-454
        if (githubUrl != null && !githubUrl.isBlank()) {
            messageBuilder.append("Slack body includes GitHub issue: ").append(githubUrl);
        } else {
            messageBuilder.append("No GitHub URL provided.");
        }

        String fullMessage = messageBuilder.toString();

        // Use the port to send
        return slackPort.sendNotification(fullMessage);
    }
}
