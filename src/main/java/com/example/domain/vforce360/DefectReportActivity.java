package com.example.domain.vforce360;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Temporal Activity implementation for handling defect reporting logic.
 * This is the "Real" implementation that interacts with the SlackNotificationPort.
 */
@Component
public class DefectReportActivity {

    private static final Logger log = LoggerFactory.getLogger(DefectReportActivity.class);
    private static final String GITHUB_ISSUE_BASE_URL = "https://github.com/example/bank-of-z/issues/";
    private static final String SLACK_CHANNEL_ID = "C-vforce360-issues";

    private final SlackNotificationPort slackNotificationPort;

    @Autowired
    public DefectReportActivity(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Reports a defect to the VForce360 Slack channel.
     * Generates the message body including the GitHub issue URL.
     *
     * @param defectId The ID of the defect (e.g., "VW-454").
     */
    public void reportDefect(String defectId) {
        log.info("Reporting defect: {}", defectId);

        String githubUrl = GITHUB_ISSUE_BASE_URL + defectId;
        // Construct the message body ensuring the URL is present as per acceptance criteria.
        String messageBody = String.format("Defect Reported: %s. Please investigate: %s", defectId, githubUrl);

        slackNotificationPort.postMessage(SLACK_CHANNEL_ID, messageBody);

        log.info("Defect {} reported to Slack channel {}", defectId, SLACK_CHANNEL_ID);
    }
}