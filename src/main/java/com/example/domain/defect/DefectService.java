package com.example.domain.defect;

import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.ReportDefectCmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Domain service for handling defect reporting logic.
 * Orchestrates the creation of GitHub issues (simulated here) and ensures
 * the resulting URL is propagated to Slack notifications.
 */
@Service
public class DefectService {

    private static final Logger log = LoggerFactory.getLogger(DefectService.class);

    /**
     * Handles the ReportDefect command.
     * 1. Generates the GitHub Issue URL.
     * 2. Emits the DefectReportedEvent containing the URL.
     * <p>
     * This implementation fixes VW-454 by ensuring the URL is stringified
     * and available in the event payload for Slack consumption.
     *
     * @param cmd The command to report a defect.
     * @return The resulting domain event.
     */
    public DefectReportedEvent handleReportDefect(ReportDefectCmd cmd) {
        log.info("Reporting defect {}: {}", cmd.defectId(), cmd.title());

        // Simulate GitHub API interaction
        // In a real implementation, this would call the GitHub REST API
        // to create an issue in the repository specified by the project ID.
        String githubIssueUrl = createGithubIssue(
            cmd.projectId(),
            cmd.title(),
            cmd.description()
        );

        log.info("GitHub issue created: {}", githubIssueUrl);

        // Emit event with the URL
        return new DefectReportedEvent(
            cmd.defectId(),
            githubIssueUrl,
            Instant.now()
        );
    }

    /**
     * Simulates the creation of a GitHub issue.
     * Returns a deterministic URL based on the defect ID for testing.
     */
    protected String createGithubIssue(String projectId, String title, String description) {
        // VForce360 expects a standard GitHub URL structure.
        // Format: https://github.com/{org}/{repo}/issues/{id}
        // Since we don't have a real GitHub client, we construct a mock URL.
        return String.format("https://github.com/bank-of-z/vforce360/issues/%s", projectId);
    }
}
