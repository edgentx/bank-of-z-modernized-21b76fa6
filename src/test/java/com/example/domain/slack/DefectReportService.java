package com.example.domain.slack;

import com.example.ports.SlackNotificationPort;

/**
 * Service to handle defect reporting logic.
 * This class acts as the System Under Test (SUT). 
 * In a real scenario, this might be a Temporal Activity or Workflow implementation.
 */
public class DefectReportService {

    private final SlackNotificationPort slackNotificationPort;
    private final String githubBaseUrl = "https://github.com/egdcrypto/bank-of-z/issues/"; 

    public DefectReportService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Reports a defect to the engineering team via Slack.
     * Corresponds to 'Trigger _report_defect via temporal-worker exec'.
     *
     * @param defectId The ID of the defect (e.g., VW-454).
     * @param description The description of the defect.
     */
    public void reportDefect(String defectId, String description) {
        // Construct the GitHub URL based on the defect ID
        // Note: In a real scenario, mapping logic might be more complex.
        // For VW-454, we assume the ID maps directly to the issue number or we extract it.
        String issueNumber = defectId.replace("VW-", "").replace("S-FB-", "");
        String fullUrl = githubBaseUrl + issueNumber;

        // Construct the Slack payload
        // Expected Format (Bug Fix target): Include the link explicitly
        String payload = String.format(
            "{\n  \"text\": \"Defect Reported: %s\",\n  \"blocks\": [\n    {\n      \"type\": \"section\",\n      \"text\": {\n        \"type\": \"mrkdwn\",\n        \"text\": \"*Description:* %s\\n*GitHub issue:* <%s|Link>\"\n      }\n    }\n  ]\n}",
            defectId, description, fullUrl
        );

        try {
            slackNotificationPort.send(payload);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send Slack notification", e);
        }
    }
}
