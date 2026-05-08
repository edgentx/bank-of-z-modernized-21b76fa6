package com.example.application;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCommand;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Service implementation orchestrating the Defect Reporting Workflow.
 * Corresponds to the 'temporal-worker exec' logic in the story.
 * <p>
 * Logic:
 * 1. Receive command.
 * 2. Use Aggregate to validate state.
 * 3. Use GitHubPort to create issue and get URL.
 * 4. Use SlackPort to notify, ensuring the URL is in the body.
 */
@Service
public class DefectWorkflowService {

    private final GitHubPort gitHubPort;
    private final SlackNotificationPort slackPort;

    public DefectWorkflowService(GitHubPort gitHubPort, SlackNotificationPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    public void handleReportDefect(ReportDefectCommand cmd) {
        // 1. Initialize Aggregate
        DefectAggregate aggregate = new DefectAggregate(cmd.defectId());

        // 2. Execute Command (Validation)
        aggregate.execute(cmd);

        // 3. Call GitHub
        String issueUrl = gitHubPort.reportIssue(aggregate.getTitle(), aggregate.getDescription());

        // 4. Construct Slack Payload
        // Expected: "Slack body includes GitHub issue: <url>"
        // We ensure the URL is appended to the message body.
        StringBuilder slackBody = new StringBuilder();
        slackBody.append("Defect Reported: ").append(aggregate.getTitle()).append("\n");
        slackBody.append("Description: ").append(aggregate.getDescription()).append("\n");
        slackBody.append("Severity: ").append(aggregate.getSeverity()).append("\n");
        slackBody.append("GitHub Issue: <").append(issueUrl).append(">"); // Slack link format

        // 5. Send Notification
        // In a real scenario, we might infer the channel from config, but story implies #vforce360-issues
        slackPort.sendMessage("#vforce360-issues", slackBody.toString());
    }
}
