package com.example.application;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Application Service responsible for orchestrating the defect reporting use case.
 * It handles the execution of the command via the Aggregate and uses the
 * SlackNotificationPort to dispatch the resulting message.
 */
@Service
public class DefectReportingService {

    private final SlackNotificationPort slackNotificationPort;

    public DefectReportingService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Handles the ReportDefectCommand.
     * 1. Instantiates the Aggregate.
     * 2. Executes the command.
     * 3. Uses the resulting event payload to notify via Slack.
     *
     * @param cmd The command containing defect details.
     */
    public void reportDefect(ReportDefectCmd cmd) {
        var aggregate = new DefectAggregate(cmd.defectId());

        // Execute command logic (validation and event creation)
        var events = aggregate.execute(cmd);

        // Process events (In a CQRS system, we might persist events here, but for
        // this S-FB-1 fix, we primarily care that the notification is sent).
        events.forEach(event -> {
            if (event instanceof com.example.domain.defect.model.DefectReportedEvent reportedEvent) {
                // Trigger the side-effect (Slack notification)
                slackNotificationPort.send(reportedEvent.notificationBody());
            }
        });
    }
}