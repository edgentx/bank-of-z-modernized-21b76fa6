package com.example.application;

import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCommand;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.ports.GitHubIssueTrackerPort;
import com.example.ports.SlackNotifierPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Application Service / Orchestrator for the Validation Workflow.
 * Implements S-FB-1 logic:
 * 1. Receives command.
 * 2. Executes Aggregate.
 * 3. Calls GitHub Adapter.
 * 4. Calls Slack Adapter with GitHub URL.
 */
@Service
public class ValidationWorkflowOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(ValidationWorkflowOrchestrator.class);
    private final GitHubIssueTrackerPort githubPort;
    private final SlackNotifierPort slackPort;

    public ValidationWorkflowOrchestrator(GitHubIssueTrackerPort githubPort, SlackNotifierPort slackPort) {
        this.githubPort = githubPort;
        this.slackPort = slackPort;
    }

    /**
     * Handles the ReportDefectCommand workflow.
     */
    public DefectReportedEvent handleReportDefect(ValidationAggregate aggregate, ReportDefectCommand cmd) {
        // 1. Execute Domain Logic
        var events = aggregate.execute(cmd);
        if (events.isEmpty()) {
            throw new IllegalStateException("Aggregate did not produce an event");
        }
        
        // In a strict ES architecture, we'd persist the event here.
        // For this defect fix, we focus on the Integration flow.
        
        // 2. External Interaction: GitHub
        String issueUrl;
        try {
            issueUrl = githubPort.createIssue(cmd.title(), cmd.description());
            log.info("GitHub issue created: {}", issueUrl);
        } catch (Exception e) {
            log.error("Failed to create GitHub issue", e);
            // Depending on requirements, we might fail the workflow or post a failure notice to Slack.
            // For S-FB-1, we assume the GitHub creation is successful as per the Happy Path.
            throw new RuntimeException("Failed to report defect to GitHub", e);
        }

        // 3. External Interaction: Slack
        // Requirement: Slack body includes GitHub issue URL.
        String slackChannel = "#vforce360-issues";
        String slackBody = String.format(
            "Defect Reported: %s\nSeverity: %s\nGitHub Issue: %s",
            cmd.title(), cmd.severity(), issueUrl
        );

        try {
            slackPort.postMessage(slackChannel, slackBody);
            log.info("Slack notification sent to {}", slackChannel);
        } catch (Exception e) {
            log.error("Failed to post to Slack", e);
            // If Slack fails, we still have the GitHub URL, so we might consider the defect "reported".
            // But for the purpose of S-FB-1, we want to ensure the link IS in the body.
        }

        // 4. Return Enriched Event
        // Note: The aggregate event might have a placeholder URL. 
        // We return a version with the actual confirmed URL for downstream consumers if necessary.
        return new DefectReportedEvent(
            aggregate.id(),
            cmd.defectId(),
            issueUrl,
            slackChannel,
            java.time.Instant.now()
        );
    }
}
