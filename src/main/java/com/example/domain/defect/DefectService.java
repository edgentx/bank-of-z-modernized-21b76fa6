package com.example.domain.defect;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Service handling defect reporting logic.
 * This class acts as the workflow implementation triggered by the Temporal worker.
 */
@Service
public class DefectService {

    private final SlackNotificationPort slackNotificationPort;

    /**
     * Constructor for dependency injection.
     *
     * @param slackNotificationPort The port implementation for sending Slack notifications.
     */
    public DefectService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Executes the report_defect workflow.
     * Constructs the message body and delegates to the Slack port.
     *
     * @param issueUrl The URL of the created GitHub issue.
     */
    public void reportDefect(String issueUrl) {
        String messageBody = constructSlackBody(issueUrl);
        slackNotificationPort.sendMessage(messageBody);
    }

    /**
     * Constructs the Slack message body containing the defect details.
     * Pattern: "Defect Reported. GitHub Issue: <url>"
     *
     * @param url The GitHub issue URL.
     * @return The formatted message string.
     */
    private String constructSlackBody(String url) {
        return "Defect Reported. GitHub Issue: " + url;
    }
}
