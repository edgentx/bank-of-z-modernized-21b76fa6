package com.example.mocks;

import com.example.adapters.SlackNotificationPort;
import com.example.domain.shared.DomainEvent;
import com.example.ports.GitHubPort;
import java.util.List;
import java.util.ArrayList;

/**
 * Mock Repository that simulates the persistence and side-effect triggering
 * for VForce360 aggregates. This allows the test to run without a real DB.
 */
public class InMemoryVForce360Repository {

    private final GitHubPort gitHubPort;
    private final SlackNotificationPort slackPort;

    public InMemoryVForce360Repository(GitHubPort gitHubPort, SlackNotificationPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    public void save(List<DomainEvent> events) {
        if (events == null || events.isEmpty()) {
            return;
        }

        // In a real implementation, we would save to DB here.
        // In this mock, we trigger the side effects (GitHub & Slack)
        // based on the events raised by the Aggregate.

        for (DomainEvent event : events) {
            if ("DefectReported".equals(event.type())) {
                handleDefectReported(event);
            }
        }
    }

    private void handleDefectReported(DomainEvent event) {
        // 1. Create GitHub Issue
        // Extract data from event (mocked for Red Phase structure)
        String issueUrl = gitHubPort.createIssue(
            "[vw-454] Validating VW-454", 
            "Project: 21b76fa6...", 
            "bug"
        );

        // 2. Send Slack Notification with URL
        String slackBody = String.format(
            "New defect reported: %s\nGitHub Issue: %s",
            "VW-454",
            issueUrl
        );
        slackPort.sendNotification(slackBody);
    }
}
