package com.example.mocks;

public class MockDefectReportingActivitiesImpl {
    private String lastSlackMessage;
    private String githubUrlToReturn = "https://github.com/test/issues/1";

    public String createGitHubIssue(String description) {
        // Simulate creating an issue and returning a URL
        return githubUrlToReturn;
    }

    public void notifySlack(String messageBody) {
        this.lastSlackMessage = messageBody;
    }

    public String getLastSlackMessage() {
        return lastSlackMessage;
    }

    public void setGithubUrlToReturn(String url) {
        this.githubUrlToReturn = url;
    }
}