package com.example.workflows;

import com.example.domain.validation.ReportDefectCmd;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import io.temporal.workflow.Workflow;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the defect reporting workflow.
 * Ensures that the GitHub URL is correctly propagated to Slack.
 */
public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    private final GitHubPort gitHubPort;
    private final SlackPort slackPort;

    public ReportDefectWorkflowImpl() {
        this.gitHubPort = Workflow.newActivityStub(GitHubPort.class);
        this.slackPort = Workflow.newActivityStub(SlackPort.class);
    }

    @Override
    public void report(ReportDefectCmd cmd) {
        // Step 1: Create Issue in GitHub
        String issueUrl = gitHubPort.createIssue(cmd.defectId(), cmd.title(), cmd.description());

        // Step 2: Notify Slack with the GitHub URL
        Map<String, Object> context = new HashMap<>();
        context.put("channel", "#vforce360-issues");
        context.put("projectId", cmd.projectId());
        
        // CRITICAL FIX for S-FB-1: Ensure the URL is included in the body
        String body = cmd.description() + "\n\nGitHub Issue: " + issueUrl;
        context.put("body", body);

        slackPort.sendMessage(context);
    }
}
