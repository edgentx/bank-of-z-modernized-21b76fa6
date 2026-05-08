package com.example.validation;

import com.example.ports.SlackPort;
import com.example.validation.model.DefectReportCommand;

/**
 * Validator service responsible for processing defect reports and
 * ensuring they are formatted correctly for Slack notifications.
 * 
 * Story: S-FB-1 (Fix: Validating VW-454)
 * Role: Constructs the Slack body with the required GitHub URL.
 */
public class SlackNotificationValidator {

    private static final String GITHUB_BASE_URL = "https://github.com/BankOfZ/VForce360/issues/";
    private final SlackPort slackPort;

    public SlackNotificationValidator(SlackPort slackPort) {
        this.slackPort = slackPort;
    }

    /**
     * Processes the defect report command and triggers a Slack notification.
     * 
     * Implementation logic (TDD Green Phase):
     * 1. Generate GitHub issue URL based on the defect ID.
     * 2. Format the Slack body text including the defect details and the URL.
     * 3. Invoke the Slack port.
     *
     * @param command The defect report command containing ID and project context.
     */
    public void processDefectReport(DefectReportCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("DefectReportCommand cannot be null");
        }
        
        String defectId = command.defectId();
        if (defectId == null || defectId.isBlank()) {
            throw new IllegalArgumentException("defectId cannot be null or blank");
        }

        // Construct the GitHub URL as per requirements
        String githubUrl = GITHUB_BASE_URL + defectId;

        // Construct the payload body
        // The test checks for the presence of "http" and the specific defectId
        StringBuilder payloadBuilder = new StringBuilder();
        payloadBuilder.append("*Defect Reported:* ").append(command.projectKey()).append(" - ").append(defectId).append("\n");
        payloadBuilder.append("*Description:* ").append(command.description() != null ? command.description() : "No description provided").append("\n");
        payloadBuilder.append("*GitHub Issue:* ").append(githubUrl).append("\n");

        // Send notification via the port
        slackPort.sendNotification(payloadBuilder.toString());
    }
}
