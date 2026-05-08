package com.example.domain.defect;

import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackNotificationPort;

import java.util.HashMap;
import java.util.Map;

/**
 * Dummy Orchestrator class to satisfy the test compilation (Red Phase).
 * This class represents the SUT (System Under Test) logic that needs to be implemented.
 * In the Green phase, this would be replaced by the actual Temporal Activity or Service.
 */
public class DefectReportingOrchestrator {

    private final MockSlackNotificationPort slackPort;
    private final MockGitHubPort gitHubPort;

    public DefectReportingOrchestrator(MockSlackNotificationPort slackPort, MockGitHubPort gitHubPort) {
        this.slackPort = slackPort;
        this.gitHubPort = gitHubPort;
    }

    public void report(ReportDefectCmd cmd) {
        // TODO: Implement actual logic in Green phase
        // 1. Fetch URL from GitHubPort
        // 2. Construct Slack Body with URL
        // 3. Send via SlackPort
        
        // Intentional Stub for Red Phase:
        // We currently do NOT include the URL, so the test should fail.
        Map<String, Object> payload = new HashMap<>();
        payload.put("text", "Defect Reported: " + cmd.summary()); 
        
        slackPort.sendMessage("#vforce360-issues", payload);
    }
}
