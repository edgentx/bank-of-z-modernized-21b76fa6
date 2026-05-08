package com.example.domain.defect;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Orchestrator class (The System Under Test).
 * This class represents the logic triggered by the Temporal worker (`_report_defect`).
 * 
 * GREEN PHASE IMPLEMENTATION:
 * Fulfills the workflow of reporting a defect, creating an issue, and notifying Slack.
 */
public class DefectReportOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(DefectReportOrchestrator.class);
    private static final String DEFAULT_CHANNEL = "#vforce360-issues";

    private final SlackPort slackPort;
    private final GitHubPort gitHubPort;

    public DefectReportOrchestrator(SlackPort slackPort, GitHubPort gitHubPort) {
        this.slackPort = slackPort;
        this.gitHubPort = gitHubPort;
    }

    /**
     * Main workflow logic.
     * 1. Create GitHub Issue.
     * 2. Notify Slack with the URL.
     */
    public void reportDefect(String defectId, String title) {
        log.info("Reporting defect {} - {}", defectId, title);

        // Step 1: Create the issue in GitHub
        // We use the defectId as the title prefix to ensure the mock can find the stub
        // and the real adapter would create a meaningful title.
        String issueUrl = gitHubPort.createIssue(defectId + ": " + title, "Defect reported by VForce360");

        // Step 2: Prepare the Slack notification
        // The body must include the GitHub URL to satisfy VW-454
        String slackBody = String.format("Defect reported: %s%nDetails: %s", issueUrl, title);
        
        Map<String, String> context = new HashMap<>();
        context.put("defect_id", defectId);
        context.put("source", "vforce360");

        // Step 3: Send notification
        slackPort.sendNotification(DEFAULT_CHANNEL, slackBody, context);
    }
}
