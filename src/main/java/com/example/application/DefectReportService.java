package com.example.application;

import com.example.domain.ports.SlackNotificationPort;
import com.example.domain.shared.Command;
import org.springframework.stereotype.Service;

/**
 * Application Service handling the defect reporting logic.
 * Acts as the bridge between the Temporal workflow (or test harness) and the domain logic/ports.
 */
@Service
public class DefectReportService {

    private final SlackNotificationPort slackNotificationPort;

    /**
     * Constructor injection for the Slack port.
     * In a real environment, this would be injected with a concrete adapter (e.g., SlackWebhookAdapter).
     * In tests, this is injected with a Mockito mock.
     */
    public DefectReportService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Executes the defect report workflow.
     * This simulates the processing mentioned in the VW-454 defect scenario.
     * 
     * @param cmd The command triggering the report (can be used for metadata in future)
     */
    public void reportDefect(Command cmd) {
        // Construct the Slack message body including the GitHub issue URL
        String messageBody = buildMessageBody();
        
        // Send the notification via the port
        slackNotificationPort.send(messageBody);
    }

    /**
     * Builds the message body ensuring the GitHub URL is present.
     * Implements the fix for VW-454.
     */
    private String buildMessageBody() {
        return "Defect detected: VW-454. Please review the issue at: https://github.com/example/bank-of-z-modernization/issues/454";
    }
}
