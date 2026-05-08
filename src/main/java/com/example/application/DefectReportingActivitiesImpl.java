package com.example.application;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.springframework.stereotype.Component;

@Component
public class DefectReportingActivitiesImpl implements DefectReportingActivities {

    private final GitHubPort gitHubPort;
    private final SlackPort slackPort;

    public DefectReportingActivitiesImpl(GitHubPort gitHubPort, SlackPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    @Override
    public String createGitHubIssue(String title, String body) {
        try {
            return gitHubPort.createIssue(title, body).get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create GitHub issue", e);
        }
    }

    @Override
    public void notifySlack(String channel, String message) {
        try {
            slackPort.sendMessage(channel, message).get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to send Slack message", e);
        }
    }
}