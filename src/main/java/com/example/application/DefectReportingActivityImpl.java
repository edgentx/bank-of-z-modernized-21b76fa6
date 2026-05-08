package com.example.application;

import com.example.ports.GitHubPort;
import com.example.ports.NotificationPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Implementation of the DefectReportingActivity.
 * Bridges Temporal activities with the Domain Ports (Adapters).
 */
@Component
public class DefectReportingActivityImpl implements DefectReportingActivity {

    private final GitHubPort gitHubPort;
    private final NotificationPort notificationPort;

    @Autowired
    public DefectReportingActivityImpl(GitHubPort gitHubPort, NotificationPort notificationPort) {
        this.gitHubPort = gitHubPort;
        this.notificationPort = notificationPort;
    }

    @Override
    public String createGitHubIssue(String title, String body) {
        return gitHubPort.createIssue(title, body);
    }

    @Override
    public void notifySlack(String githubUrl) {
        // VW-454: Ensure the URL is explicitly passed in the message body.
        String messageBody = "GitHub Issue Created: " + githubUrl;
        notificationPort.send(new SlackMessage(messageBody));
    }
}
