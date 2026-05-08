package com.example.workflows;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Temporal Workflow implementation for reporting defects.
 * Orchestrates creating a GitHub issue and notifying Slack.
 */
@WorkflowImpl(taskQueues = "DefectReportTaskQueue")
public class DefectReportWorkflowImpl implements DefectReportWorkflow {

    private static final Logger log = LoggerFactory.getLogger(DefectReportWorkflowImpl.class);

    // Workflow stubs for Activities
    private final GitHubPort github;
    private final SlackPort slack;

    // Temporal requires a default constructor for instantiating workflows.
    // We use Workflow.newActivityStub to get the implementations at runtime.
    public DefectReportWorkflowImpl() {
        // In a real Temporal setup, these are stubs defined by @ActivityInterface options.
        // For this validation scenario, we rely on the Spring context providing the beans
        // or the Test Environment providing the mocks.
        // This constructor is primarily for the Worker registry.
        this.github = Workflow.newActivityStub(GitHubPort.class);
        this.slack = Workflow.newActivityStub(SlackPort.class);
    }

    @Override
    public String reportDefect(String title, String body) {
        log.info("Executing defect report workflow for: {}", title);

        // 1. Create Issue in GitHub
        String url = github.createIssue(title, body);

        // 2. Notify Slack
        // Expected format: "Defect Reported: <title>. View: <url>"
        String slackMessage = String.format("Defect Reported: %s. View: %s", title, url);
        slack.sendMessage(slackMessage);

        return url;
    }
}