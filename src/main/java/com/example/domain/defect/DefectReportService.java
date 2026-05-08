package com.example.domain.defect;

import com.example.ports.GitHubRepositoryPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Domain Service responsible for orchestrating defect reporting.
 * This aligns with the temporal-worker logic described in the defect report.
 */
@Service
public class DefectReportService {

    private final GitHubRepositoryPort gitHubRepository;
    private final SlackNotificationPort slackNotification;

    /**
     * Constructor-based injection for Ports.
     */
    public DefectReportService(GitHubRepositoryPort gitHubRepository, SlackNotificationPort slackNotification) {
        this.gitHubRepository = gitHubRepository;
        this.slackNotification = slackNotification;
    }

    /**
     * Reports a defect by creating a GitHub issue and notifying Slack.
     * This method ensures the Slack body contains the GitHub URL (Fix for VW-454).
     *
     * @param projectId The project ID
     * @param defectId The defect ID (e.g. VW-454)
     * @param summary The defect summary
     * @param description The defect description
     */
    public void reportDefect(String projectId, String defectId, String summary, String description) {
        // 1. Create GitHub Issue
        String issueUrl = gitHubRepository.createIssue(projectId, defectId, summary, description);

        // 2. Notify Slack with the GitHub link appended
        // This ensures the 'Actual Behavior' matches 'Expected Behavior' in VW-454.
        String slackBody = description + "\n\nGitHub Issue: " + issueUrl;
        
        slackNotification.sendDefectReport(projectId, defectId, summary, slackBody);
    }
}