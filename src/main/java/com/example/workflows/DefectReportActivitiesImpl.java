package com.example.workflows;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import io.temporal.activity.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of DefectReportActivities.
 * This class orchestrates the calls to the external ports (GitHub, Slack).
 */
public class DefectReportActivitiesImpl implements DefectReportActivities {

    private static final Logger log = LoggerFactory.getLogger(DefectReportActivitiesImpl.class);

    private final GitHubPort gitHubPort;
    private final SlackNotificationPort slackPort;

    public DefectReportActivitiesImpl(GitHubPort gitHubPort, SlackNotificationPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    @Override
    public String createGitHubIssue(String title, String body) {
        log.info("Creating GitHub issue with title: {}", title);
        return gitHubPort.createIssue(title, body);
    }

    @Override
    public void notifySlack(String channel, String issueUrl) {
        log.info("Sending Slack notification to {} with URL {}", channel, issueUrl);

        // Fix for VW-454: Ensure the URL is part of the message text.
        Map<String, Object> payload = new HashMap<>();
        payload.put("text", "New defect reported: " + issueUrl);

        slackPort.sendNotification(channel, payload);
    }
}
