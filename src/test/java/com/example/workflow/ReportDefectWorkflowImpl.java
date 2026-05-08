package com.example.workflow;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import io.temporal.workflow.Workflow;

/**
 * Implementation of the defect reporting workflow.
 * In the 'Red Phase' of TDD, this implementation is missing or stubbed,
 * causing the tests to fail until the logic is added.
 * 
 * THIS CLASS IS INTENTIONALLY STUBBED/EMPTY to ensure TEST FAILS initially.
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
        // TDD RED PHASE:
        // Logic to post to Slack with the GitHub URL is missing.
        // The test expects the URL in the body.
        // Workflow.sleep(1000); // Simulate processing
        // slack.postMessage("#vforce360-issues", "Issue created: " + url);
    }
}
