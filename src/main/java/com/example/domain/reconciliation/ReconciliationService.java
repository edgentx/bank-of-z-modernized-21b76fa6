package com.example.domain.reconciliation;

import com.example.domain.reconciliation.model.ReportDefectCmd;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Domain Service handling Reconciliation logic,
 * including the '_report_defect' workflow triggered by Temporal.
 */
@Service
public class ReconciliationService {

    private static final Logger log = LoggerFactory.getLogger(ReconciliationService.class);
    private static final String SLACK_CHANNEL = "#vforce360-issues";
    private static final String GITHUB_ISSUE_BASE_URL = "https://github.com/bank-of-z/issues/";

    private final SlackNotificationPort slackNotificationPort;

    public ReconciliationService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Executes the defect reporting workflow.
     * Corresponds to the temporal-worker exec trigger.
     *
     * @param cmd The command containing defect details.
     */
    public void reportDefect(ReportDefectCmd cmd) {
        log.info("Reporting defect {} to Slack channel {}", cmd.defectId(), SLACK_CHANNEL);

        String messageBody = formatSlackMessage(cmd.defectId());

        slackNotificationPort.sendMessage(SLACK_CHANNEL, messageBody);
    }

    /**
     * Formats the Slack message body ensuring the GitHub issue URL is present.
     * Fixes defect VW-454.
     *
     * @param defectId The ID of the defect (e.g., "VW-454")
     * @return A string containing the formatted message with the URL.
     */
    private String formatSlackMessage(String defectId) {
        // Ensuring the link is explicitly present as per the defect report requirements
        String url = GITHUB_ISSUE_BASE_URL + defectId;
        return String.format("Defect reported: %s. View details at %s", defectId, url);
    }
}
