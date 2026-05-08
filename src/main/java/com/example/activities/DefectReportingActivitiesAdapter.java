package com.example.activities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Adapter implementation for DefectReportingActivities.
 * This class contains the "real" logic for communicating with external systems.
 * It is separate from the Interface definition to satisfy Spring/Temporal wiring patterns.
 */
@Component
public class DefectReportingActivitiesAdapter implements DefectReportingActivitiesImpl {

    private static final Logger log = LoggerFactory.getLogger(DefectReportingActivitiesAdapter.class);

    @Override
    public String createGitHubIssue(String description) {
        // Placeholder for actual GitHub API logic
        log.info("[ADAPTER] Creating GitHub issue for description: {}", description);
        // In a real scenario, this would use RestTemplate/WebClient to call GitHub API
        return "https://github.com/bank-of-z/vforce360/issues/454";
    }

    @Override
    public void notifySlack(String messageBody) {
        // Placeholder for actual Slack Webhook logic
        log.info("[ADAPTER] Sending Slack notification: {}", messageBody);
        // In a real scenario, this would POST to a Slack Webhook URL
    }
}