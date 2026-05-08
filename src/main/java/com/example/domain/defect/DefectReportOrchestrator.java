package com.example.domain.defect;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import java.util.HashMap;
import java.util.Map;

/**
 * Orchestrator class (The System Under Test).
 * This class represents the logic triggered by the Temporal worker (`_report_defect`).
 * 
 * In the TDD Red phase, this class is intentionally a stub/placeholder,
 * containing just enough structure to be compiled and invoked by the tests,
 * but missing the logic required to make the assertions pass.
 */
public class DefectReportOrchestrator {

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
        // RED PHASE IMPLEMENTATION:
        // This method is currently empty or incorrect, causing the tests to fail.
        // In the Green phase, this will be implemented to:
        // 1. String url = gitHubPort.createIssue(title, ...);
        // 2. String body = "Defect reported: " + url;
        // 3. slackPort.sendNotification("#vforce360-issues", body, ...);
        
        // Intentional NO-OP for Red Phase
    }
}
