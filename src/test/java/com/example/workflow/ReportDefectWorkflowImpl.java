package com.example.workflow;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import io.temporal.workflow.Workflow;

/**
 * Implementation of the defect reporting workflow.
 * Orchestrates the generation of a GitHub URL and posting a notification to Slack.
 */
public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    private final SlackPort slack;
    private final GitHubPort gitHub;

    public ReportDefectWorkflowImpl(SlackPort slack, GitHubPort gitHub) {
        this.slack = slack;
        this.gitHub = gitHub;
    }

    @Override
    public void reportDefect(String issueId, String description) {
        // Generate the GitHub URL using the provided port
        String url = gitHub.createIssueUrl(issueId);
        
        // Construct the message body as required by the test
        String body = "Defect reported. GitHub issue: " + url;
        
        // Post the message to the specific channel
        slack.postMessage("#vforce360-issues", body);
    }
}
