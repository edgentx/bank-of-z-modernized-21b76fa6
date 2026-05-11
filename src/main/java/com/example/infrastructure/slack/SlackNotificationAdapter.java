package com.example.infrastructure.slack;

import com.example.application.ports.SlackNotificationPort;
import com.example.domain.validation.model.ReportDefectCmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the Slack Notification Port.
 * In a production environment, this would use the Slack Web API client.
 * For S-FB-1, this validates the contract and formatting of the message body.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void postDefectNotification(ReportDefectCmd cmd, String githubIssueUrl) {
        // Construct the Slack message body payload.
        // This format is critical for the regression test VW-454.
        String messageBody = buildSlackBody(cmd, githubIssueUrl);

        // Simulate the external API call
        log.info("Sending Slack notification: {}", messageBody);
        
        // Real implementation would involve:
        // MethodsClient methods = slackClient.methods();
        // ChatPostMessageRequest request = ChatPostMessageRequest.builder()
        //     .channel(channelId)
        //     .text(messageBody)
        //     .build();
        // methods.chatPostMessage(request);
    }

    /**
     * Builds the formatted string for the Slack message.
     * Ensures the GitHub URL is embedded in the text.
     */
    private String buildSlackBody(ReportDefectCmd cmd, String githubIssueUrl) {
        return String.format(
            "Defect Reported: %s - %s\nIssue: %s",
            cmd.defectId(),
            cmd.title(),
            githubIssueUrl
        );
    }
}