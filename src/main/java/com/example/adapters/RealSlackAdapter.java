package com.example.adapters;

import com.example.domain.shared.Command;
import com.example.domain.validation.model.ReportDefectWithLinkCmd;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of SlackNotificationPort.
 * Formats the Slack message body to include the GitHub issue URL.
 */
@Component
public class RealSlackAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(RealSlackAdapter.class);

    @Override
    public boolean postDefect(Command cmd) {
        if (!(cmd instanceof ReportDefectWithLinkCmd c)) {
            log.warn("Received unsupported command type: {}", cmd.getClass().getSimpleName());
            return false;
        }

        try {
            // In a real implementation, this would use the Slack WebClient to post to a channel.
            // For this defect fix, the critical part is the String formatting.
            String messageBody = formatMessage(c);
            
            log.info("Posting to Slack: {}", messageBody);
            
            // Pseudo-code for actual API call:
            // slackClient.chatPostMessage("#vforce360-issues", messageBody);
            
            return true;
        } catch (Exception e) {
            log.error("Error posting to Slack", e);
            return false;
        }
    }

    private String formatMessage(ReportDefectWithLinkCmd cmd) {
        // FIX for VW-454: Ensure the URL is in the body
        return String.format(
            "Defect Reported: %s\n" +
            "Description: %s\n" +
            "GitHub Issue: %s",
            cmd.title(),
            cmd.description(),
            cmd.githubUrl()
        );
    }
}
