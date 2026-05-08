package com.example.domain.notification;

import com.example.ports.GitHubIssueTrackerPort;
import com.example.ports.SlackNotificationPort;

/**
 * Service class (Placeholder for RED phase).
 * In TDD, we write the test first (which imports this), 
 * so we create a stub that will fail compilation or runtime checks initially.
 * 
 * This will be implemented in the Green phase.
 */
public class DefectReportingService {

    private final SlackNotificationPort slack;
    private final GitHubIssueTrackerPort gitHub;

    public DefectReportingService(SlackNotificationPort slack, GitHubIssueTrackerPort gitHub) {
        this.slack = slack;
        this.gitHub = gitHub;
    }

    public void reportDefect(String title, String description, String channel) {
        // RED PHASE STUB: This currently does nothing, causing tests to fail.
        // GREEN PHASE TODO:
        // 1. Create GitHub issue via gitHub.createIssue(title, description)
        // 2. Get URL
        // 3. Post to Slack via slack.postMessage(channel, "GitHub issue: " + url)
        
        // Intentional failure for the test to detect missing URL:
        slack.postMessage(channel, "This is just a stub");
    }
}
