package com.example.workers;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.springframework.stereotype.Component;

/**
 * Implementation of the ReportDefectActivity.
 * Delegates logic to the domain ports (adapters).
 */
@Component
public class ReportDefectActivityImpl implements ReportDefectActivity {

    private final GitHubPort githubPort;
    private final SlackPort slackPort;

    public ReportDefectActivityImpl(GitHubPort githubPort, SlackPort slackPort) {
        this.githubPort = githubPort;
        this.slackPort = slackPort;
    }

    @Override
    public String createGitHubIssue(String title, String description, String component) {
        return githubPort.createIssue(title, description, component);
    }

    @Override
    public void sendSlackNotification(String channel, String messageBody) {
        slackPort.sendMessage(channel, messageBody);
    }
}
