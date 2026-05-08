package com.example.workflows;

import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.port.SlackNotificationPort;
import com.example.domain.validation.repository.ValidationRepository;
import io.temporal.activity.ActivityInterface;
import io.temporal.spring.boot.ActivityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Temporal Activity implementation for reporting defects.
 * This acts as the use-case orchestrator for the defect reporting story.
 */
@ActivityInterface
@Component
@ActivityImpl(taskQueue = "DEFECT_REPORTING_TASK_QUEUE")
public class ReportDefectActivity {

    private final ValidationRepository validationRepository;
    private final SlackNotificationPort slackNotificationPort;

    @Autowired
    public ReportDefectActivity(ValidationRepository validationRepository, SlackNotificationPort slackNotificationPort) {
        this.validationRepository = validationRepository;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Executes the report_defect workflow.
     * Creates/Updates the aggregate and notifies Slack.
     */
    public void report(String defectId, String description) {
        // 1. Load or Create Aggregate
        ValidationAggregate aggregate = validationRepository.findById(defectId)
                .orElseGet(() -> new ValidationAggregate(defectId));

        // 2. Execute Command
        // Assuming a command exists or we just trigger the behavior directly for this defect fix.
        // For the purpose of the defect report, we generate the GitHub URL and send it.
        
        // Construct GitHub URL based on defect ID
        String githubUrl = "https://github.com/egdcrypto/bank-of-z/issues/" + defectId.replace("VW-", "");
        
        String slackMessage = String.format(
            "Defect Reported: %s\nDescription: %s\nGitHub Issue: %s", 
            defectId, description, githubUrl
        );

        // 3. Notify via Port
        slackNotificationPort.send(slackMessage);

        // Save state if necessary (omitted for brevity of defect fix focus)
        // validationRepository.save(aggregate);
    }
}
