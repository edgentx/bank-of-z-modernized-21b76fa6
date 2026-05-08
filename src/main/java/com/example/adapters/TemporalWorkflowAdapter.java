package com.example.adapters;

import com.example.ports.SlackNotifier;
import com.example.ports.TemporalWorkflowStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real adapter for Temporal workflows.
 * Implements the defect reporting logic defined in VW-454.
 */
@Component
public class TemporalWorkflowAdapter implements TemporalWorkflowStarter {

    private static final Logger log = LoggerFactory.getLogger(TemporalWorkflowAdapter.class);
    private final SlackNotifier slackNotifier;

    // Configuration for URL construction (Could be externalized to application.properties)
    private static final String GITHUB_BASE_URL = "https://github.com/example/bank-of-z-modernization/issues/";

    public TemporalWorkflowAdapter(SlackNotifier slackNotifier) {
        this.slackNotifier = slackNotifier;
    }

    @Override
    public void reportDefect(String defectId, String description) {
        // Validation Logic
        if (defectId == null || defectId.isBlank()) {
            log.error("Validation failed: Defect ID cannot be blank.");
            throw new IllegalArgumentException("Defect ID cannot be blank");
        }

        if (description == null || description.isBlank()) {
            log.error("Validation failed: Description cannot be blank.");
            throw new IllegalArgumentException("Description cannot be blank");
        }

        // Fix for VW-454: Construct the GitHub URL
        String fullUrl = GITHUB_BASE_URL + defectId;

        // Construct the message body
        String messageBody = String.format(
            "Defect Reported: %s%nLink: %s",
            description,
            fullUrl
        );

        // Delegate to the Slack port
        log.info("Triggering Slack notification for defect: {}", defectId);
        slackNotifier.sendNotification(messageBody);
    }
}
