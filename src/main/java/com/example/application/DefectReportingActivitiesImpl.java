package com.example.application;

import com.example.ports.GitHubPort;
import com.example.ports.NotificationPort;
import com.example.vforce.shared.ReportDefectCommand;
import io.temporal.spring.boot.ActivityImpl;

/**
 * Implementation of the Defect Reporting Activity.
 * This fixes the build error by providing a concrete class.
 */
@ActivityImpl(taskQueue = "DefectReportingTaskQueue")
public class DefectReportingActivitiesImpl implements DefectReportingActivities {

    private final GitHubPort gitHubPort;
    private final NotificationPort notificationPort;

    public DefectReportingActivitiesImpl(GitHubPort gitHubPort, NotificationPort notificationPort) {
        this.gitHubPort = gitHubPort;
        this.notificationPort = notificationPort;
    }

    @Override
    public void reportDefect(ReportDefectCommand command) {
        // Step 1: Create GitHub Issue
        var issueLink = gitHubPort.createIssue(command);

        // Step 2: Enrich command/payload with the link for Slack
        // We construct a new command or update the description to include the URL
        String updatedDescription = command.description() + "\n\nGitHub Issue: " + issueLink.url();
        
        ReportDefectCommand enrichedCommand = new ReportDefectCommand(
            command.title(),
            updatedDescription,
            command.violations()
        );

        // Step 3: Send Notification
        notificationPort.notifyChannel(enrichedCommand);
    }
}