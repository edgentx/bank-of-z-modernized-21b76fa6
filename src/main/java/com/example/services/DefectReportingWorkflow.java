package com.example.services;

import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCommand;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.repository.ValidationRepository;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service/Workflow handler for reporting defects.
 * Orchestrates the Aggregate and the Slack Adapter.
 */
@Service
public class DefectReportingWorkflow {

    private static final Logger log = LoggerFactory.getLogger(DefectReportingWorkflow.class);
    private final ValidationRepository repository;
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportingWorkflow(ValidationRepository repository,
                                   SlackNotificationPort slackNotificationPort) {
        this.repository = repository;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Entry point for the Temporal workflow _report_defect.
     */
    public void executeReportDefect(ReportDefectCommand cmd) {
        log.info("Executing defect report for ID: {}", cmd.defectId());

        // Load or create aggregate
        ValidationAggregate aggregate = repository.findById(cmd.defectId())
            .orElseGet(() -> new ValidationAggregate(cmd.defectId()));

        // Execute command
        List<DefectReportedEvent> events = aggregate.execute(cmd);

        // Persist and handle side effects
        repository.save(aggregate);

        for (DefectReportedEvent event : events) {
            handleEvent(event);
        }
    }

    private void handleEvent(DefectReportedEvent event) {
        // Per VW-454, send a Slack notification containing the GitHub URL
        String messageBody = "Defect Reported: " + event.defectId() + " " + event.githubUrl();
        slackNotificationPort.sendMessage("#vforce360-issues", messageBody);
    }
}
