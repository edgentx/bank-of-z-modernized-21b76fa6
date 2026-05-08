package com.example.domain.vforce360.service;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.repository.ValidationRepository;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service handling the logic for reporting defects and notifying via Slack.
 * Coordinates the Domain Aggregates and Adapters.
 */
@Service
public class DefectValidationService {

    private static final Logger log = LoggerFactory.getLogger(DefectValidationService.class);
    private final ValidationRepository validationRepository;
    private final SlackNotificationPort slackNotificationPort;

    public DefectValidationService(ValidationRepository validationRepository,
                                   SlackNotificationPort slackNotificationPort) {
        this.validationRepository = validationRepository;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Handles the defect reporting workflow.
     * 1. Creates/Updates the DefectAggregate.
     * 2. Generates the message with GitHub URL.
     * 3. Sends notification via Slack Port.
     */
    public void reportDefect(ReportDefectCmd cmd) {
        // 1. Process Defect
        DefectAggregate defect = new DefectAggregate(cmd.defectId());
        var events = defect.execute(cmd);
        
        // In a real app, we would persist events here
        if (!events.isEmpty()) {
            var event = events.get(0);
            log.info("Defect processed: {}", event.aggregateId());
            
            // 2. Prepare Slack Message
            // This is the fix for VW-454: Ensure the URL is included in the body
            String slackBody = String.format(
                "Defect %s reported: %s. GitHub Issue: %s",
                cmd.defectId(), 
                cmd.description(), 
                defect.getGithubIssueUrl()
            );

            // 3. Send Notification
            slackNotificationPort.notify(slackBody);
        }
    }
}