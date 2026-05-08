package com.example.services;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.defect.repository.DefectRepository;
import com.example.ports.IssueTrackerPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Service for handling defect reporting logic.
 * Orchestrates Aggregate, Issue Tracker, and Slack Notification.
 */
@Service
public class DefectReportingService {

    private final DefectRepository defectRepository;
    private final IssueTrackerPort issueTrackerPort;
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportingService(DefectRepository defectRepository,
                                  IssueTrackerPort issueTrackerPort,
                                  SlackNotificationPort slackNotificationPort) {
        this.defectRepository = defectRepository;
        this.issueTrackerPort = issueTrackerPort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Handles the ReportDefectCmd process flow.
     */
    public void reportDefect(ReportDefectCmd cmd) {
        // 1. Load or create Aggregate
        DefectAggregate aggregate = defectRepository.findById(cmd.defectId());
        if (aggregate == null) {
            aggregate = new DefectAggregate(cmd.defectId());
        }

        // 2. Execute Command (Validations)
        aggregate.execute(cmd);

        // 3. Call External Port (GitHub)
        String gitHubUrl = issueTrackerPort.createIssue(cmd.defectId(), cmd.title());

        // 4. Construct the final Event with the external URL
        // We create a new event instance specifically for the notification/projector
        DefectReportedEvent eventWithUrl = new DefectReportedEvent(
            cmd.defectId(),
            cmd.title(),
            gitHubUrl,
            cmd.severity(),
            java.time.Instant.now()
        );

        // 5. Save Aggregate State
        defectRepository.save(aggregate);

        // 6. Notify (Handler Logic)
        // Formatting the message as expected by the tests
        String messageBody = String.format("Defect Reported: %s\nGitHub Issue: %s", cmd.title(), gitHubUrl);
        slackNotificationPort.postMessage(messageBody);
    }
}
