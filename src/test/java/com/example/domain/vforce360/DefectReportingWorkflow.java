package com.example.domain.vforce360;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import com.example.domain.vforce360.model.ReportDefectCommand;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of the Workflow/Logic.
 * This file represents the 'Implementation Under Test' context.
 * Currently contains stubs to ensure tests fail (Red phase).
 */
public class DefectReportingWorkflow {

    private final GitHubPort gitHubPort;
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportingWorkflow(GitHubPort gitHubPort, SlackNotificationPort slackNotificationPort) {
        this.gitHubPort = gitHubPort;
        this.slackNotificationPort = slackNotificationPort;
    }

    public void execute(ReportDefectCommand cmd) {
        // TODO: Implement logic to satisfy VW-454
        // 1. Get URL from GitHubPort
        // 2. Construct Slack Body
        // 3. Send via SlackNotificationPort
        
        // Intentional Red Phase Stub:
        // sendNotification(new HashMap<>()); // This would fail the specific body assertion
    }
}