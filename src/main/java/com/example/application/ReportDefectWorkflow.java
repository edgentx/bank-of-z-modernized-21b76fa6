package com.example.application;

import com.example.domain.reporting.model.DefectAggregate;
import com.example.domain.reporting.model.ReportDefectCmd;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * Application Service orchestrating the reporting of a defect.
 * This acts as the bridge between the Domain Logic (Aggregate), external ports (GitHub, Slack),
 * and the Temporal workflow trigger.
 */
@Service
public class ReportDefectWorkflow {

    private static final Logger log = LoggerFactory.getLogger(ReportDefectWorkflow.class);

    private final GitHubIssuePort gitHubIssuePort;
    private final SlackNotificationPort slackNotificationPort;

    // Constructor Injection for testability and immutability
    public ReportDefectWorkflow(GitHubIssuePort gitHubIssuePort, SlackNotificationPort slackNotificationPort) {
        this.gitHubIssuePort = gitHubIssuePort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Main entry point triggered by Temporal Worker.
     * <p>
     * Process:
     * 1. Validate command via Aggregate.
     * 2. Fetch URL from GitHub Port.
     * 3. Construct formatted message.
     * 4. Post to Slack Port.
     *
     * @param cmd The command containing defect details
     * @return true if workflow completed successfully
     */
    public boolean executeReportDefect(ReportDefectCmd cmd) {
        // 1. Domain Logic Validation
        DefectAggregate aggregate = new DefectAggregate(cmd.defectId());
        try {
            aggregate.execute(cmd);
        } catch (Exception e) {
            log.error("Failed to validate defect command: {}", cmd.defectId(), e);
            throw e;
        }

        // 2. Fetch Context (GitHub URL)
        String issueId = cmd.defectId(); // Using defectId as the issueId correlation
        Optional<String> urlOpt = gitHubIssuePort.getIssueUrl(issueId);

        // VW-454 Validation Logic: We require the URL to be present to proceed.
        if (urlOpt.isEmpty()) {
            log.error("GitHub URL could not be resolved for issue {}", issueId);
            throw new IllegalStateException("Cannot report defect: GitHub URL not found for " + issueId);
        }

        String url = urlOpt.get();

        // 3. Construct Message Body (The Fix)
        // The body MUST explicitly include the GitHub URL string.
        String messageBody = String.format(
            "Defect Reported: %s\nGitHub Issue: %s",
            issueId,
            url
        );

        // 4. Send Notification
        Map<String, String> metadata = Map.of(
            "issueId", issueId,
            "severity", cmd.severity(),
            "component", cmd.component()
        );

        boolean success = slackNotificationPort.postMessage(messageBody, metadata);
        
        if (success) {
            log.info("Defect {} reported successfully.", issueId);
        } else {
            log.error("Failed to send notification for defect {}.", issueId);
        }
        
        return success;
    }
}
