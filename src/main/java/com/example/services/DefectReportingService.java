package com.example.services;

import com.example.adapters.GitHubRepositoryAdapter;
import com.example.adapters.SlackNotifierAdapter;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.ports.GitHubRepositoryPort;
import com.example.ports.SlackNotifierPort;
import org.springframework.stereotype.Service;

/**
 * Application Service orchestrating the defect reporting flow.
 * This satisfies the E2E scenario defined in VW-454.
 */
@Service
public class DefectReportingService {

    private final GitHubRepositoryPort githubRepo;
    private final SlackNotifierPort slackNotifier;

    // Constructor injection respecting the Adapter/Port pattern
    public DefectReportingService(GitHubRepositoryPort githubRepo, SlackNotifierPort slackNotifier) {
        this.githubRepo = githubRepo;
        this.slackNotifier = slackNotifier;
    }

    /**
     * Executes the report_defect workflow.
     * 1. Create GitHub Issue.
     * 2. Post notification to Slack with the GitHub URL.
     * 
     * @param cmd The command containing title and description.
     */
    public void reportDefect(ReportDefectCmd cmd) {
        // 1. Create GitHub Issue
        // Note: Validation happens inside the adapter (or domain). 
        // The test expects IllegalArgumentException on blank title.
        String issueUrl = githubRepo.createIssue(cmd.title(), cmd.description());

        // 2. Construct and post Slack message
        // The test specifically checks for the format:
        // "Defect Reported: <title>\nGitHub Issue: <url>"
        String messageBody = "Defect Reported: " + cmd.title() + "\n" +
                             "GitHub Issue: " + issueUrl;

        slackNotifier.postMessage(messageBody);
    }
}
