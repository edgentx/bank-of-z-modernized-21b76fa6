package com.example.application;

import com.example.domain.validation.model.*;
import com.example.domain.validation.repository.ValidationRepository;
import com.example.domain.slack.SlackMessage;
import com.example.ports.SlackNotifier;

/**
 * Orchestrator for the Validation Workflow (Temporal Worker).
 * Handles the logic for reporting defects and notifying Slack.
 */
public class ValidationWorkflowOrchestrator {

    private final ValidationRepository validationRepository;
    private final SlackNotifier slackNotifier;

    public ValidationWorkflowOrchestrator(ValidationRepository validationRepository, SlackNotifier slackNotifier) {
        this.validationRepository = validationRepository;
        this.slackNotifier = slackNotifier;
    }

    /**
     * Report a defect, create a GitHub issue (simulated), and notify Slack.
     * 
     * @param validationId The ID of the validation check that failed.
     * @param defectId The ID of the defect report.
     * @param githubIssueUrl The URL of the created GitHub issue.
     */
    public void reportDefect(String validationId, String defectId, String githubIssueUrl) {
        if (githubIssueUrl == null || githubIssueUrl.isBlank()) {
            throw new IllegalArgumentException("GitHub Issue URL cannot be blank");
        }

        // 1. Load or create Aggregate
        ValidationAggregate aggregate = validationRepository.findById(validationId)
            .orElse(new ValidationAggregate(validationId));

        // 2. Execute Domain Logic
        aggregate.execute(new ReportDefectCmd(validationId, defectId));
        aggregate.execute(new LinkGitHubIssueCmd(validationId, githubIssueUrl));

        // 3. Persist
        validationRepository.save(aggregate);

        // 4. Notify Slack
        String messageBody = String.format(
            "Defect Reported: %s for Validation: %s. GitHub Issue: %s",
            defectId, validationId, githubIssueUrl
        );
        
        slackNotifier.send(new SlackMessage("#vforce360-issues", messageBody));
    }
}
