package com.example.adapters;

import com.example.domain.defect.model.ReportDefectCommand;
import com.example.workflow.DefectActivities;
import org.springframework.stereotype.Component;

/**
 * Implementation of DefectActivities.
 * This is where the defect reporting logic (GitHub + Slack) lives.
 * This is currently a STUB to satisfy compilation errors.
 */
@Component
public class DefectActivitiesImpl implements DefectActivities {

    private final GitHubPort gitHubPort;
    private final SlackNotifierPort slackNotifier;

    public DefectActivitiesImpl(GitHubPort gitHubPort, SlackNotifierPort slackNotifier) {
        this.gitHubPort = gitHubPort;
        this.slackNotifier = slackNotifier;
    }

    @Override
    public String reportDefect(ReportDefectCommand cmd) {
        // 1. Create GitHub Issue
        String url = gitHubPort.createIssue(cmd.title(), cmd.description())
                .orElseThrow(() -> new RuntimeException("Failed to create GitHub issue"));

        // 2. Notify Slack (S-FB-1: Verify this contains the URL)
        String message = "Defect Reported: " + cmd.title() + " - " + url;
        slackNotifier.sendNotification("#vforce360-issues", message);

        return url;
    }
}