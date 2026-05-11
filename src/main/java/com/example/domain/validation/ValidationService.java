package com.example.domain.validation;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.springframework.stereotype.Service;

/**
 * Service responsible for orchestrating the defect reporting workflow.
 * This represents the "Temporal Worker" logic in a simplified form for this validation fix.
 */
@Service
public class ValidationService {

    private final GitHubPort gitHubPort;
    private final SlackPort slackPort;

    public ValidationService(GitHubPort gitHubPort, SlackPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    /**
     * Executes the report_defect workflow.
     * 1. Creates an issue in GitHub.
     * 2. Sends a notification to Slack with the GitHub URL.
     *
     * @param defectId The ID of the defect being reported.
     * @return true if the workflow completed successfully.
     */
    public boolean executeReportDefect(String defectId) {
        if (defectId == null || defectId.isBlank()) {
            throw new IllegalArgumentException("Defect ID cannot be null or empty");
        }

        // Step 1: Create GitHub Issue
        String issueUrl = gitHubPort.createIssue(defectId, "Defect: " + defectId);

        // Step 2: Send Slack notification containing the GitHub URL
        // The expected format for the link in the body is <url|label> or <url>
        String slackMessage = String.format(
                "New defect reported: %s\nGitHub Issue: <%s|GitHub Issue>",
                defectId,
                issueUrl
        );

        return slackPort.sendMessage("#vforce360-issues", slackMessage);
    }
}
