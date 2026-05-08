package com.example.workflow;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.springframework.stereotype.Component;

/**
 * Implementation of DefectActivities.
 * Acts as an Adapter between the Temporal Workflow engine and our Domain Ports.
 */
@Component
public class DefectActivitiesImpl implements DefectActivities {

    private final GitHubPort gitHubPort;
    private final SlackPort slackPort;

    public DefectActivitiesImpl(GitHubPort gitHubPort, SlackPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    @Override
    public String createGitHubIssue(String title, String body) {
        return gitHubPort.createIssue(title, body);
    }

    @Override
    public void notifySlack(String defectTitle, String githubUrl) {
        // Fix for VW-454: Construct the body ensuring the link is present.
        String messageBody = String.format(
            "Defect Reported: %s\nGitHub Issue: %s",
            defectTitle,
            githubUrl
        );
        slackPort.sendMessage("#vforce360-issues", messageBody);
    }
}
