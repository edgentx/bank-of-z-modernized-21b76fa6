package com.example.domain.validation;

import com.example.domain.shared.ValidationReportedEvent;
import com.example.ports.GitHubIntegrationPort;
import com.example.ports.SlackNotificationPort;

/**
 * Workflow implementation for handling defect reports.
 * Orchestrates the retrieval of GitHub issue URLs and Slack notifications.
 */
public class DefectReportingWorkflow {

    private final SlackNotificationPort slackPort;
    private final GitHubIntegrationPort githubPort;

    public DefectReportingWorkflow(SlackNotificationPort slackPort, GitHubIntegrationPort githubPort) {
        this.slackPort = slackPort;
        this.githubPort = githubPort;
    }

    /**
     * Handles the defect reporting process.
     * 1. Retrieves the GitHub URL for the defect.
     * 2. Constructs the Slack message body including the URL.
     * 3. Sends the notification via SlackPort.
     *
     * @param event The validation reported event triggering the workflow.
     */
    public void handleDefectReport(ValidationReportedEvent event) {
        // 1. Retrieve URL from GitHub service
        // Note: We use the aggregateId from the event as the defect reference ID
        String defectId = event.aggregateId();
        var urlOpt = githubPort.getIssueUrl(defectId);
        
        // 2. Construct Body
        // The requirement is: "Slack body includes GitHub issue: <url>"
        String messageBody;
        if (urlOpt.isPresent()) {
            String url = urlOpt.get();
            messageBody = "GitHub issue: " + url;
        } else {
            // Fallback if URL is missing, though the primary AC expects the URL.
            // We include the description to provide context.
            messageBody = "Validation Reported: " + event.description() + " (No GitHub URL found)";
        }

        // 3. Send Notification
        // If the Slack adapter throws an exception (e.g., network timeout),
        // we let it propagate to the Temporal workflow for retry handling.
        slackPort.sendNotification(messageBody);
    }
}
