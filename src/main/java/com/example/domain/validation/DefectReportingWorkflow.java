package com.example.domain.validation;

import com.example.domain.shared.ReportDefectCommand;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Workflow implementation for reporting defects.
 * This orchestrates fetching the issue details from GitHub and notifying Slack.
 */
public class DefectReportingWorkflow {

    private static final Logger log = LoggerFactory.getLogger(DefectReportingWorkflow.class);

    private final GitHubPort gitHubPort;
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportingWorkflow(GitHubPort gitHubPort, SlackNotificationPort slackNotificationPort) {
        this.gitHubPort = gitHubPort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Executes the defect reporting logic.
     * Corresponds to the _report_defect temporal worker activity/flow.
     *
     * @param command The command containing defect details.
     * @throws IllegalStateException if the GitHub URL cannot be resolved.
     */
    public void executeReportDefect(ReportDefectCommand command) {
        String defectId = command.defectId();
        log.info("Executing report defect for ID: {}", defectId);

        // 1. Fetch URL from GitHub Port
        Optional<String> urlOpt = gitHubPort.getIssueUrl(defectId);
        if (urlOpt.isEmpty()) {
            log.error("GitHub URL not found for defect: {}", defectId);
            throw new IllegalStateException("GitHub URL not found for defect: " + defectId);
        }

        // 2. Construct Message Body
        String messageBody = constructMessageBody(defectId, urlOpt.get());

        // 3. Send via Slack Port
        // Assuming the target channel is fixed for VForce360 issues as per scenario
        boolean success = slackNotificationPort.postMessage("#vforce360-issues", messageBody);
        if (!success) {
            log.error("Failed to post message to Slack for defect: {}", defectId);
            throw new RuntimeException("Failed to post to Slack for defect: " + defectId);
        }

        log.info("Successfully reported defect {} to Slack.", defectId);
    }

    private String constructMessageBody(String defectId, String url) {
        return "Defect Reported: " + defectId + "\n" +
               "Link: " + url;
    }
}
