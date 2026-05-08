package com.example.workflows;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;

/**
 * Implementation of Temporal Activities.
 * This class is registered with the Temporal Worker and executes the workflow steps.
 */
public class ReportDefectActivitiesImpl implements ReportDefectActivities {

    private final GitHubPort gitHubPort;
    private final SlackPort slackPort;

    public ReportDefectActivitiesImpl(GitHubPort gitHubPort, SlackPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    @Override
    public String createGitHubIssue(String summary, String description) {
        return gitHubPort.createIssue(summary, description);
    }

    @Override
    public void notifySlack(String message) {
        slackPort.sendMessage(message);
    }
}
