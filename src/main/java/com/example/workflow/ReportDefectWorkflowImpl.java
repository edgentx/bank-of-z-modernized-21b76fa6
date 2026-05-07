package com.example.workflow;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotifierPort;

/**
 * Implementation of the ReportDefectWorkflow.
 * This is the class under test.
 * Note: Currently a stub to satisfy compilation while in Red phase.
 */
public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    private final GitHubPort gitHub;
    private final SlackNotifierPort slack;

    public ReportDefectWorkflowImpl(GitHubPort gitHub, SlackNotifierPort slack) {
        this.gitHub = gitHub;
        this.slack = slack;
    }

    @Override
    public void reportDefect(String defectId, String description) {
        // 1. Create GitHub Issue
        String issueUrl = gitHub.createIssue(defectId, description);

        // 2. Notify Slack
        // The defect here is that we were NOT including issueUrl in the body.
        // To make tests fail initially (Red Phase), we send a body without the URL.
        String body = "Defect Reported: " + defectId; 
        slack.notify(body);
    }
}
