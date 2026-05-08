package com.example.validation;

import com.example.domain.shared.ReportDefectCmd;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;

/**
 * Temporal Workflow implementation (Stub for TDD Red Phase).
 * This implementation intentionally fails the AC to satisfy the "Red" phase requirement.
 */
public class ValidationWorkflowImpl implements ValidationWorkflow {

    private final SlackNotificationPort slack;
    private final GitHubIssuePort github;

    public ValidationWorkflowImpl(SlackNotificationPort slack, GitHubIssuePort github) {
        this.slack = slack;
        this.github = github;
    }

    @Override
    public void reportDefect(ReportDefectCmd cmd) {
        // Intentionally incorrect implementation to trigger test failure.
        // This mimics the "Actual Behavior" where the link is missing.
        String brokenMessage = "Defect Reported: " + cmd.title();
        slack.sendMessage(brokenMessage);
    }
}
