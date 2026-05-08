package com.example.application;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotifierPort;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Interface for the Defect Reporting Activity.
 * Defines the contract for reporting defects (creating GitHub issues + notifying Slack).
 */
@ActivityInterface
public interface DefectReportingActivityInterface {

    @ActivityMethod
    String execute(String title, String description);

    /**
     * Implementation of the Activity Interface.
     * This implementation is used in the test context to wire the mocks.
     */
    class Impl implements DefectReportingActivities {
        private final GitHubPort gitHubPort;
        private final SlackNotifierPort slackNotifierPort;

        public Impl(GitHubPort gitHubPort, SlackNotifierPort slackNotifierPort) {
            this.gitHubPort = gitHubPort;
            this.slackNotifierPort = slackNotifierPort;
        }

        @Override
        public String execute(String title, String description) {
            // 1. Create GitHub Issue
            String issueUrl = gitHubPort.createIssue(title, description);
            
            // 2. Notify Slack with the URL
            String message = "Defect Reported: " + title + "\nGitHub Issue: " + issueUrl;
            slackNotifierPort.notify(message);
            
            return issueUrl;
        }
    }
}