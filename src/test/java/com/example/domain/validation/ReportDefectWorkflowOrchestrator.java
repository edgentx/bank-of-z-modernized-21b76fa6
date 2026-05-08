package com.example.domain.validation;

import com.example.ports.VForce360NotificationPort;
import org.springframework.stereotype.Component;

/**
 * Orchestrator for reporting defects.
 * This acts as the Workflow implementation in the TDD context.
 * Fixed to include the GitHub URL in the Slack body.
 */
@Component
public class ReportDefectWorkflowOrchestrator {

    private final VForce360NotificationPort notificationPort;
    private static final String GITHUB_REPO_BASE_URL = "https://github.com/example/bank-of-z/issues/";

    public ReportDefectWorkflowOrchestrator(VForce360NotificationPort notificationPort) {
        this.notificationPort = notificationPort;
    }

    /**
     * Reports a defect to VForce360/Slack with the GitHub URL.
     * 
     * @param defectId The ID of the defect (e.g. "VW-454").
     */
    public void reportDefect(String defectId) {
        if (defectId == null || defectId.isBlank()) {
            throw new IllegalArgumentException("defectId cannot be null or blank");
        }

        // Extract the numeric ID part (e.g., "454" from "VW-454")
        // This logic assumes format VW-{numeric_id}
        String issueId = defectId.split("-")[1]; 
        
        String githubUrl = GITHUB_REPO_BASE_URL + issueId;
        
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("Defect Reported: ").append(defectId).append("\n");
        bodyBuilder.append("GitHub Issue: ").append(githubUrl).append("\n");
        
        String finalBody = bodyBuilder.toString();

        notificationPort.reportDefect(defectId, finalBody);
    }
}
