package com.example.domain.defect;

import com.example.ports.IssueTrackerPort;
import com.example.ports.NotificationPort;
import org.springframework.stereotype.Service;

/**
 * Service handling the defect reporting workflow.
 * This fixes S-FB-1 by ensuring the GitHub URL is included in the Slack notification.
 */
@Service
public class DefectService {

    private final IssueTrackerPort issueTracker;
    private final NotificationPort notificationPort;
    private static final String SLACK_CHANNEL = "#vforce360-issues";

    public DefectService(IssueTrackerPort issueTracker, NotificationPort notificationPort) {
        this.issueTracker = issueTracker;
        this.notificationPort = notificationPort;
    }

    /**
     * Reports a defect, creating an external issue and notifying Slack.
     * This is the implementation required to pass the S-FB-1 scenario.
     *
     * @param defectId The ID of the defect (e.g. VW-454).
     * @param description The description of the defect.
     */
    public void reportDefect(String defectId, String description) {
        // Step 1: Create the GitHub issue (or Jira, etc.)
        String issueUrl = issueTracker.createIssue(defectId, description);

        // Step 2: Notify Slack including the URL (Fix for S-FB-1)
        // Previously, the URL might have been omitted.
        String messageBody = String.format("Defect reported: %s%nIssue Link: %s", defectId, issueUrl);
        notificationPort.sendMessage(SLACK_CHANNEL, messageBody);
    }
}
