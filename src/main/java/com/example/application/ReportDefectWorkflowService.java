package com.example.application;

import com.example.ports.GithubPort;
import com.example.ports.SlackPort;

import java.util.HashMap;
import java.util.Map;

public class ReportDefectWorkflowService {

    private final GithubPort githubPort;
    private final SlackPort slackPort;

    public ReportDefectWorkflowService(GithubPort githubPort, SlackPort slackPort) {
        this.githubPort = githubPort;
        this.slackPort = slackPort;
    }

    public void reportDefect(String title, String description) {
        // 1. Create GitHub Issue
        String githubUrl = githubPort.createIssue(title, description);

        // 2. Notify Slack with the URL included in the body
        // Expected Behavior: Slack body includes GitHub issue: <url>
        Map<String, String> slackMessage = new HashMap<>();
        slackMessage.put("text", "Defect reported: " + title + "\nGitHub issue: " + githubUrl);

        // Send to specific channel mentioned in defect report
        slackPort.sendMessage("#vforce360-issues", slackMessage);
    }
}
