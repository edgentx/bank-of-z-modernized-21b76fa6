package com.example.application;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import io.temporal.spring.boot.ActivityImpl;
import org.springframework.stereotype.Component;

/**
 * Temporal Activity Implementation for Defect Reporting.
 * Stub to fix compilation.
 */
@Component
@ActivityImpl(taskQueues = "DefectReportingTaskQueue")
public class DefectReportingActivities implements DefectReportingActivityInterface {

    private final GitHubPort gitHubClient;
    private final SlackPort slackClient;

    public DefectReportingActivities(GitHubPort gitHubClient, SlackPort slackClient) {
        this.gitHubClient = gitHubClient;
        this.slackClient = slackClient;
    }

    @Override
    public String reportDefect(String title, String body) {
        // Real implementation would call gitHubClient.createIssue(...)
        // Then slackClient.postMessage(...)
        return "STUB_ID";
    }
}
