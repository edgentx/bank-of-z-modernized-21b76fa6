package com.example.application.validation;

import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.ports.GitHubIssueTracker;
import com.example.ports.SlackNotifier;

/**
 * Application Service orchestrating the defect reporting workflow.
 * This is the System Under Test (SUT) for the E2E test.
 */
public class ValidationService {

    private final GitHubIssueTracker gitHub;
    private final SlackNotifier slack;

    public ValidationService(GitHubIssueTracker gitHub, SlackNotifier slack) {
        this.gitHub = gitHub;
        this.slack = slack;
    }

    /**
     * Handles the report_defect workflow.
     * 1. Process domain logic
     * 2. Call GitHub (Port)
     * 3. Call Slack (Port)
     */
    public void reportDefect(ReportDefectCmd cmd) {
        // 1. Domain Logic
        var aggregate = new ValidationAggregate(cmd.validationId());
        // The aggregate would handle invariants, but for E2E wiring we need to produce the side effects
        aggregate.execute(cmd); 

        // 2. External Integration (GitHub)
        String issueUrl = gitHub.createIssue("vforce360", "Defect: " + cmd.issueReference(), cmd.description());

        // 3. Notification (Slack)
        String slackMessage = String.format(
            "New defect reported: %s\nSeverity: %s\nGitHub issue: %s",
            cmd.issueReference(),
            cmd.severity(),
            issueUrl
        );

        slack.send(slackMessage);
    }
}
