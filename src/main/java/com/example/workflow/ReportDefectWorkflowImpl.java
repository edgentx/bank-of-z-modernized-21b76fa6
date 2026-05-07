package com.example.workflow;

import com.example.domain.shared.DefectReportedEvent;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotifierPort;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the ReportDefectWorkflow.
 * This class orchestrates the reporting of a defect by creating an issue in GitHub
 * and notifying a Slack channel with the resulting URL.
 */
public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    private final GitHubPort gitHub;
    private final SlackNotifierPort slack;
    private final List<DefectReportedEvent> eventLog = new ArrayList<>();

    public ReportDefectWorkflowImpl(GitHubPort gitHub, SlackNotifierPort slack) {
        this.gitHub = gitHub;
        this.slack = slack;
    }

    @Override
    public void reportDefect(String defectId, String description) {
        // 1. Create GitHub Issue
        String issueUrl = gitHub.createIssue(defectId, description);

        // 2. Emit Domain Event (Internal state management)
        DefectReportedEvent event = new DefectReportedEvent(defectId, issueUrl, Instant.now());
        eventLog.add(event);

        // 3. Notify Slack
        // FIX FOR S-FB-1: Ensure the issueUrl is included in the Slack body.
        String body = "Defect Reported: " + defectId + "\nGitHub Issue: " + issueUrl;
        slack.notify(body);
    }

    // Exposed for testing/validation purposes if needed, though not part of the primary interface
    public List<DefectReportedEvent> getEventLog() {
        return new ArrayList<>(eventLog);
    }
}
