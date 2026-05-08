package com.example.domain.vforce;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Service handling the logic for reporting defects.
 * This is the implementation class required to make the VW-454 tests pass.
 */
@Service
public class DefectReportingService {

    private final SlackNotificationPort slackNotificationPort;

    // Constructor injection as per Adapter pattern requirements
    public DefectReportingService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Executes the defect reporting workflow.
     * Creates the domain event, formats the message, and sends it via the Slack port.
     *
     * @param cmd The command containing defect details.
     * @return The event that was recorded.
     */
    public DefectReportedEvent reportDefect(ReportDefectCommand cmd) {
        // 1. Create the domain event
        DefectReportedEvent event = new DefectReportedEvent(
                cmd.getDefectId(),
                cmd.getTitle(),
                cmd.getGithubUrl(),
                Instant.now()
        );

        // 2. Build the formatted message body
        // This format matches the expectation in VW454SlackLinkValidationTest.buildExpectedMessage
        String messageBody = buildExpectedMessage(
                event.getDefectId(),
                event.getTitle(),
                event.getGithubUrl()
        );

        // 3. Send notification
        slackNotificationPort.send(messageBody);

        return event;
    }

    /**
     * Helper method to construct the Slack message body.
     * Visibility is package-private for potential unit testing, though the Service is tested via integration.
     */
    String buildExpectedMessage(String id, String title, String url) {
        return String.format(
                "Defect Alert: %s\nID: %s\nGitHub Issue: %s",
                title, id, url
        );
    }
}
