package com.example.domain.vforce360;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import com.example.domain.vforce360.model.ReportDefectCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of the Defect Reporting Workflow.
 * Satisfies requirements for Story S-FB-1 (VW-454).
 */
public class DefectReportingWorkflow {

    private final GitHubPort gitHubPort;
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportingWorkflow(GitHubPort gitHubPort, SlackNotificationPort slackNotificationPort) {
        this.gitHubPort = gitHubPort;
        this.slackNotificationPort = slackNotificationPort;
    }

    public void execute(ReportDefectCommand cmd) {
        // 1. Get URL from GitHubPort
        Optional<String> urlOpt = gitHubPort.getIssueUrl(cmd.defectId());

        // 2. Construct Slack Body
        StringBuilder body = new StringBuilder();
        body.append("Defect Report: ").append(cmd.defectId());
        
        if (urlOpt.isPresent()) {
            body.append("\nGitHub Issue: ").append(urlOpt.get());
        } else {
            body.append("\n(GitHub Issue URL not found for ID: ").append(cmd.defectId()).append(")");
        }

        body.append("\nDescription: ").append(cmd.description());

        // 3. Send via SlackNotificationPort
        Map<String, Object> payload = new HashMap<>();
        payload.put("text", body.toString());
        
        this.slackNotificationPort.sendNotification(payload);
    }
}