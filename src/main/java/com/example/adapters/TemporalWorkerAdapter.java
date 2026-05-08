package com.example.adapters;

import com.example.domain.shared.Command;
import com.example.domain.validation.ReportDefectCommand;
import com.example.domain.validation.ValidationAggregate;
import com.example.domain.validation.repository.ValidationRepository;
import com.example.ports.SlackNotificationPort;
import com.example.ports.TemporalWorkerPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Real adapter for TemporalWorkerPort.
 * Orchestrates the domain logic to report defects.
 * This simulates the Temporal Activity/Workflow logic entry point.
 */
public class TemporalWorkerAdapter implements TemporalWorkerPort {

    private static final Logger logger = LoggerFactory.getLogger(TemporalWorkerAdapter.class);
    private static final String GITHUB_BASE_URL = "https://github.com/example-org/repo/issues/";
    private static final String SLACK_CHANNEL = "#vforce360-issues";

    private final ValidationRepository validationRepository;
    private final SlackNotificationPort slackNotificationPort;

    public TemporalWorkerAdapter(ValidationRepository validationRepository,
                                 SlackNotificationPort slackNotificationPort) {
        this.validationRepository = validationRepository;
        this.slackNotificationPort = slackNotificationPort;
    }

    @Override
    public void reportDefect(String defectId) {
        logger.info("Workflow triggered: report_defect for ID {}", defectId);

        // 1. Load or Create Aggregate
        ValidationAggregate aggregate = validationRepository.findById(defectId)
                .orElse(new ValidationAggregate(defectId));

        // 2. Prepare Domain Command
        // We construct the expected GitHub URL as part of the command data
        String expectedUrl = GITHUB_BASE_URL + defectId;
        Command command = new ReportDefectCommand(defectId, expectedUrl, SLACK_CHANNEL);

        // 3. Execute Domain Logic
        var events = aggregate.execute(command);

        // 4. Handle Side Effects (Events)
        for (var event : events) {
            if (event.type().equals("DefectReportedEvent")) {
                handleNotification((DefectReportedEvent) event);
            }
        }

        // 5. Save Aggregate State
        validationRepository.save(aggregate);
    }

    private void handleNotification(DefectReportedEvent event) {
        logger.info("Sending notification to Slack channel {}: {}", event.channel(), event.messageBody());
        slackNotificationPort.sendMessage(event.channel(), event.messageBody());
    }
}
