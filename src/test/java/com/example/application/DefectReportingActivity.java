package com.example.application;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Activity implementation for reporting defects.
 * In the Red Phase, this is a stub/placeholder that will fail compilation or functionality
 * until the implementation is provided.
 * 
 * ACTIVITY INTERFACE
 */
public class DefectReportingActivity {

    private static final Logger log = LoggerFactory.getLogger(DefectReportingActivity.class);
    private final GitHubIssuePort gitHubPort;
    private final SlackNotificationPort slackPort;

    public DefectReportingActivity(GitHubIssuePort gitHubPort, SlackNotificationPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    public String execute(DefectReportCommand cmd) {
        log.info("Reporting defect {}", cmd.defectId());

        // 1. Create GitHub Issue
        String issueUrl = gitHubPort.createIssue(cmd.defectId(), cmd.title());

        // 2. Notify Slack
        String messageBody = String.format(
            "Defect Reported: %s\nSeverity: %s\nIssue: %s", 
            cmd.title(), 
            cmd.severity(), 
            issueUrl // CRITICAL: URL must be here
        );

        boolean sent = slackPort.sendMessage("#vforce360-issues", messageBody);
        
        return sent ? issueUrl : null;
    }
}
