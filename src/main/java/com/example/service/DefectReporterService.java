package com.example.service;

import com.example.ports.DefectReporterPort;
import com.example.ports.GitHubPort;
import org.springframework.stereotype.Service;

/**
 * Service orchestrating the defect reporting workflow.
 * Corresponds to the Temporal activity logic for _report_defect.
 */
@Service
public class DefectReporterService {

    private final GitHubPort gitHubPort;
    private final DefectReporterPort defectReporterPort;

    /**
     * Constructor for dependency injection.
     *
     * @param gitHubPort The port for interacting with GitHub.
     * @param defectReporterPort The port for notifying Slack.
     */
    public DefectReporterService(GitHubPort gitHubPort, DefectReporterPort defectReporterPort) {
        this.gitHubPort = gitHubPort;
        this.defectReporterPort = defectReporterPort;
    }

    /**
     * Executes the report defect workflow.
     * 1. Creates an issue in GitHub.
     * 2. Reports the defect (with the GitHub URL) to Slack.
     *
     * @param defectId The ID of the defect (e.g., "VW-454").
     * @param title The title of the defect.
     * @param body The description/body of the defect.
     * @return The URL of the created GitHub issue.
     */
    public String report(String defectId, String title, String body) {
        // Step 1: Create GitHub Issue
        String githubUrl = gitHubPort.createIssue(title, body);

        // Step 2: Report to Slack with the GitHub URL
        defectReporterPort.reportDefect(defectId, githubUrl);

        return githubUrl;
    }
}
