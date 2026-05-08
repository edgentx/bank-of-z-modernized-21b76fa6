package com.example.service;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.ports.SlackNotifierPort;
import org.springframework.stereotype.Service;

/**
 * Service for handling defect reporting workflow.
 * Orchestrates the domain logic and external notifications.
 */
@Service
public class DefectService {

    private final SlackNotifierPort slackNotifier;

    public DefectService(SlackNotifierPort slackNotifier) {
        this.slackNotifier = slackNotifier;
    }

    /**
     * Reports a defect by executing the domain command and notifying Slack.
     * 
     * This implementation fixes the defect where the GitHub URL was missing
     * from the Slack notification body.
     * 
     * @param cmd The command to report a defect
     * @return The generated GitHub URL
     */
    public String reportDefect(ReportDefectCmd cmd) {
        // 1. Instantiate and execute aggregate logic
        DefectAggregate aggregate = new DefectAggregate(cmd.defectId());
        var events = aggregate.execute(cmd);
        
        if (events.isEmpty()) {
            throw new IllegalStateException("Defect reporting produced no events");
        }

        // 2. Extract the specific event (Expectation: DefectReportedEvent)
        DomainEvent rawEvent = events.get(0);
        if (!(rawEvent instanceof DefectReportedEvent event)) {
             throw new IllegalStateException("Unexpected event type: " + rawEvent.getClass().getSimpleName());
        }

        // 3. Construct Slack Body with GitHub URL (Fix for VW-454)
        // Previously, the message only contained the title.
        String slackMessage = String.format(
            "Defect Reported: %s. See details at: %s",
            cmd.title(),
            event.githubUrl()
        );

        // 4. Send Notification
        slackNotifier.send(slackMessage);

        // 5. Return the URL for potential chaining or verification
        return event.githubUrl();
    }
}
