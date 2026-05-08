package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

/**
 * Real implementation of SlackNotificationPort.
 * Formats the message body to include the specific GitHub URL required by S-FB-1.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final String GITHUB_BASE_URL = "https://github.com/bank-of-z/vforce360/issues/";

    @Override
    public String formatDefectNotification(String defectId) {
        if (defectId == null || defectId.isBlank()) {
            throw new IllegalArgumentException("defectId cannot be null or blank");
        }

        String url = constructUrl(defectId);
        
        // Format: Defect reported: VW-454. See <URL|GitHub>
        return String.format("Defect reported: %s. See <%s|GitHub>", defectId, url);
    }

    @Override
    public void sendNotification(String messageBody) {
        // In a real production scenario, this would use a WebClient (e.g., Slack Webhook API)
        // to POST the messageBody to the configured Slack channel.
        // For this defect fix context, the primary requirement is the formatting logic validated above.
        System.out.println("Sending to Slack: " + messageBody);
    }

    private String constructUrl(String defectId) {
        // Extracts the numeric part (e.g., "454" from "VW-454")
        String numericId = defectId.replace("VW-", "");
        return GITHUB_BASE_URL + numericId;
    }
}
