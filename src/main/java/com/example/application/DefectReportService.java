package com.example.application;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Application Service handling the defect reporting workflow.
 * Orchestrates the Aggregate and the Notification Port.
 */
@Service
public class DefectReportService {

    private static final Logger logger = LoggerFactory.getLogger(DefectReportService.class);
    private final SlackNotificationPort slackNotificationPort;
    private static final String TARGET_CHANNEL = "#vforce360-issues";

    // Constructor injection (Spring Boot pattern)
    public DefectReportService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Executes the defect reporting logic.
     * Corresponds to the 'report_defect via temporal-worker exec' trigger.
     */
    public void reportDefect(ReportDefectCmd cmd) {
        // 1. Process via Domain Aggregate
        DefectAggregate aggregate = new DefectAggregate(cmd.defectId());
        var events = aggregate.execute(cmd);

        // 2. Handle side effects (Slack Notification)
        events.forEach(event -> {
            if (event instanceof com.example.domain.defect.model.DefectReportedEvent reportedEvent) {
                publishToSlack(reportedEvent);
            }
        });
    }

    private void publishToSlack(com.example.domain.defect.model.DefectReportedEvent event) {
        // Format the message body adhering to the VW-454 fix requirements
        String messageBody = String.format(
            "Defect Reported: %s\nGitHub Issue: <%s|View Details>",
            event.defectId(),
            event.githubUrl()
        );

        logger.info("Posting defect notification to Slack channel {}: {}", TARGET_CHANNEL, event.defectId());
        slackNotificationPort.postMessage(TARGET_CHANNEL, messageBody);
    }
}
