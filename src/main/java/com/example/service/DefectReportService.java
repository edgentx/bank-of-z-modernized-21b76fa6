package com.example.service;

import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Service handling the orchestration of defect reporting.
 * This represents the "workflow" logic triggered by the temporal-worker.
 */
@Service
public class DefectReportService {

    private final GitHubIssuePort gitHubPort;
    private final SlackNotificationPort slackPort;

    // Constructor injection allows for easy mocking in tests
    public DefectReportService(GitHubIssuePort gitHubPort, SlackNotificationPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    /**
     * Orchestrates the reporting of a defect.
     * 1. Validates command logic (via Aggregate).
     * 2. Creates GitHub issue.
     * 3. Notifies Slack with the URL.
     *
     * @param cmd The command containing defect details.
     * @return The URL of the created GitHub issue.
     */
    public String reportDefect(ReportDefectCmd cmd) {
        // 1. Domain Logic Validation
        ValidationAggregate aggregate = new ValidationAggregate("temporal-validation-agg");
        aggregate.execute(cmd);

        // 2. External Call: Create GitHub Issue
        String issueUrl = gitHubPort.createIssue(cmd.defectTitle(), cmd.defectBody());

        // 3. External Call: Notify Slack
        String slackBody = buildSlackBody(cmd.defectTitle(), issueUrl);
        slackPort.postMessage("#vforce360-issues", slackBody);

        return issueUrl;
    }

    /**
     * Constructs the Slack message body.
     * Defect VW-454 fix: Ensures the GitHub URL is explicitly included.
     */
    private String buildSlackBody(String title, String url) {
        StringBuilder sb = new StringBuilder();
        sb.append("New defect reported: ").append(title).append("\n");
        // THIS IS THE FIX for VW-454
        sb.append("GitHub issue: ").append(url).append("\n");
        sb.append("Please investigate.");
        return sb.toString();
    }
}
