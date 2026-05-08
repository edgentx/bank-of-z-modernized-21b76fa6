package com.example.infrastructure;

import com.example.domain.shared.SlackMessageValidator;
import io.temporal.activity.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TemporalActivitiesImpl implements TemporalActivities {

    private static final Logger log = LoggerFactory.getLogger(TemporalActivitiesImpl.class);

    private final SlackMessageValidator slackMessageValidator;

    public TemporalActivitiesImpl(SlackMessageValidator slackMessageValidator) {
        this.slackMessageValidator = slackMessageValidator;
    }

    @Override
    public String postToSlack(String message) {
        log.info("Activity: posting to Slack. Message content: {}", message);

        // Validation: Ensure the message contains the GitHub URL as per VW-454 requirements
        if (!slackMessageValidator.containsGitHubIssueUrl(message)) {
            Activity.raiseError("Invalid Slack Message: Missing GitHub Issue URL");
        }

        // Mock Slack API call success
        return "SLACK-OK-" + Instant.now().atZone(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_INSTANT);
    }
}