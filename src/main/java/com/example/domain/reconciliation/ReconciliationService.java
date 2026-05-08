package com.example.domain.reconciliation;

import com.example.domain.reconciliation.model.ReportDefectCmd;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Domain service handling reconciliation logic.
 * This is the implementation target for the defect S-FB-1.
 */
@Service
public class ReconciliationService {

    private final SlackNotificationPort slackNotificationPort;

    public ReconciliationService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Handles the ReportDefectCmd by formatting a message and sending it to Slack.
     * 
     * @param cmd The command containing defect details.
     */
    public void reportDefect(ReportDefectCmd cmd) {
        String messageBody = formatSlackMessage(cmd);
        slackNotificationPort.send(messageBody);
    }

    /**
     * Formats the defect report into a Slack message string.
     * This method ensures the GitHub URL is present as per S-FB-1 requirements.
     * 
     * @param cmd The command to format.
     * @return The formatted string.
     */
    private String formatSlackMessage(ReportDefectCmd cmd) {
        String id = (cmd.defectId() != null) ? cmd.defectId() : "UNKNOWN";
        String project = (cmd.projectName() != null) ? cmd.projectName() : "Unknown Project";
        String desc = (cmd.description() != null) ? cmd.description() : "No description provided.";
        
        // Constructing the URL to satisfy the validation requirement
        String url = "http://github.com/bank-of-z/issues/" + id;

        return String.format(
            "Defect Detected: %s in project %s. Details: %s. Link: %s",
            id, project, desc, url
        );
    }
}
