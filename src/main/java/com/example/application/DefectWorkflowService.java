package com.example.application;

import com.example.domain.defect.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service handling the business logic for reporting defects.
 * In a full Temporal setup, this would be the Workflow Implementation.
 */
@Service
public class DefectWorkflowService {

    private static final Logger logger = LoggerFactory.getLogger(DefectWorkflowService.class);
    private static final String SLACK_CHANNEL = "#vforce360-issues";

    private final SlackNotificationPort slackNotificationPort;

    // Constructor injection of the Port
    public DefectWorkflowService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Entry point for the temporal worker / client trigger.
     */
    public void reportDefect(ReportDefectCmd cmd) {
        logger.info("Executing _report_defect for ID: {}", cmd.defectId());

        // 1. Execute Domain Logic
        DefectAggregate aggregate = new DefectAggregate(cmd.defectId());
        var events = aggregate.execute(cmd);

        // 2. Process Events (Side effects)
        events.forEach(event -> {
            if (event instanceof com.example.domain.defect.model.DefectReportedEvent) {
                handleDefectReported((com.example.domain.defect.model.DefectReportedEvent) event);
            }
        });
    }

    private void handleDefectReported(com.example.domain.defect.model.DefectReportedEvent event) {
        logger.info("Defect Reported: {} - {}", event.defectId(), event.title());
        notifySlack(event);
    }

    private void notifySlack(com.example.domain.defect.model.DefectReportedEvent event) {
        // Format the Slack body including the GitHub URL
        // The metadata map is expected to contain the 'gitHubIssueUrl'
        Map<String, String> meta = event.metadata();
        String url = meta.getOrDefault("gitHubIssueUrl", "URL_NOT_PROVIDED");

        String body = String.format(
                "Defect Reported: *%s*\n" +
                "ID: %s\n" +
                "Description: %s\n" +
                "GitHub Issue: %s",
                event.title(),
                event.defectId(),
                event.description() != null ? event.description() : "No description",
                url
        );

        slackNotificationPort.postMessage(SLACK_CHANNEL, body);
    }
}
