package com.example.workflow;

import com.example.domain.validation.model.GitHubIssueUrl;
import com.example.domain.validation.model.SlackMessageBody;
import com.example.domain.validation.port.GitHubIssuePort;
import com.example.domain.validation.port.SlackNotificationPort;
import io.temporal.activity.Activity;
import org.springframework.stereotype.Component;

/**
 * Temporal Activity Implementation.
 * Bridges the Temporal Workflow activities with the Domain Ports (Adapters).
 */
@Component
public class DefectReportActivitiesImpl implements DefectReportActivities {

    private final GitHubIssuePort gitHubIssuePort;
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportActivitiesImpl(GitHubIssuePort gitHubIssuePort, SlackNotificationPort slackNotificationPort) {
        this.gitHubIssuePort = gitHubIssuePort;
        this.slackNotificationPort = slackNotificationPort;
    }

    @Override
    public GitHubIssueUrl createGitHubIssue(String title, String description) {
        // Delegate to the Domain Port (implemented by an Adapter)
        return gitHubIssuePort.createIssue(title, description);
    }

    @Override
    public void sendSlackNotification(SlackMessageBody body) {
        // Delegate to the Domain Port (implemented by an Adapter)
        slackNotificationPort.send(body);
    }
}
