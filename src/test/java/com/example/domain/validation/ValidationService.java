package com.example.domain.validation;

import com.example.ports.SlackNotificationPort;

/**
 * Service to handle defect reporting logic.
 * This is the System Under Test (SUT).
 * In a real scenario, this would be called by the Temporal worker activity.
 */
public class ValidationService {

    private final SlackNotificationPort slackNotifier;

    public ValidationService(SlackNotificationPort slackNotifier) {
        this.slackNotifier = slackNotifier;
    }

    /**
     * Processes a defect report and sends a notification to Slack.
     * 
     * @param defectId The ID of the defect (e.g. "VW-454")
     * @param githubUrl The URL to the GitHub issue.
     */
    public void reportDefect(String defectId, String githubUrl) {
        // Construct the Slack message body
        // Note: In the RED phase, we assume this logic exists or write the test for it.
        // This implementation is intentionally simplified or incorrect to ensure tests fail initially
        // or defined as an interface/stub if we are strictly TDDing the class structure.
        
        StringBuilder payloadBuilder = new StringBuilder();
        payloadBuilder.append("{");
        payloadBuilder.append("\"text\": \"New Defect Reported: ").append(defectId).append("\"");
        
        // The bug states that the URL might be missing. 
        // The test asserts it IS present.
        if (githubUrl != null) {
            payloadBuilder.append(", \"details\": \"").append(githubUrl).append("\"");
        }
        
        payloadBuilder.append("}");

        slackNotifier.send(payloadBuilder.toString());
    }
}
