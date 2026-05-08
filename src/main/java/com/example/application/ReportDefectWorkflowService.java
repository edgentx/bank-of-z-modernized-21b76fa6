package com.example.application;

import com.example.ports.GithubPort;
import com.example.ports.SlackPort;
import com.example.vforce.shared.ReportDefectCommand;
import org.springframework.stereotype.Service;

@Service
public class ReportDefectWorkflowService {

    private final GithubPort githubPort;
    private final SlackPort slackPort;

    public ReportDefectWorkflowService(GithubPort githubPort, SlackPort slackPort) {
        this.githubPort = githubPort;
        this.slackPort = slackPort;
    }

    public void execute(ReportDefectCommand command) {
        // 1. Create GitHub Issue
        String issueUrl = githubPort.createIssue(command);

        // 2. Send Slack notification with the link (The Fix for VW-454)
        // Defect: The URL was missing in the previous implementation.
        // Expected: Slack body includes GitHub issue: <url>
        String slackMessage = "New defect reported: " + command.title() + "\nGitHub Issue: " + issueUrl;
        slackPort.sendMessage("#vforce360-issues", slackMessage);
    }
}