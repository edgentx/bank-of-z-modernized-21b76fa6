package com.example.domain.vforce360.service;

import com.example.domain.shared.ValidationException;
import com.example.domain.vforce360.model.ValidationResult;
import com.example.ports.VForce360IntegrationPort;
import org.springframework.stereotype.Service;

/**
 * Domain Service responsible for validating defect reports.
 * This implements the logic required to satisfy the acceptance criteria for VW-454.
 */
@Service
public class DefectValidationService {

    private final VForce360IntegrationPort integrationPort;

    public DefectValidationService(VForce360IntegrationPort integrationPort) {
        this.integrationPort = integrationPort;
    }

    /**
     * Validates that the Slack body for a specific defect contains the GitHub URL.
     *
     * @param defectId     The ID of the defect (e.g., "VW-454").
     * @param channelName  The Slack channel name to check.
     * @return ValidationResult indicating pass or fail.
     */
    public ValidationResult validateGitHubUrlPresence(String defectId, String channelName) {
        // 1. Verify the defect report execution was triggered (Temporal workflow check)
        if (!integrationPort.wasDefectReportExecuted(defectId)) {
            throw new ValidationException("Defect report " + defectId + " was not executed via temporal-worker.");
        }

        // 2. Retrieve the message body from the integration port
        String slackBody = integrationPort.getLastSlackMessageBody(channelName);

        // 3. Perform the core validation check: Does the body contain a GitHub link?
        if (slackBody == null || slackBody.isBlank()) {
            return ValidationResult.invalid("Slack message body is empty or null.");
        }

        boolean hasLink = slackBody.contains("https://github.com/");
        if (!hasLink) {
            return ValidationResult.invalid("Slack body is missing the GitHub issue URL.");
        }

        return ValidationResult.valid();
    }
}
