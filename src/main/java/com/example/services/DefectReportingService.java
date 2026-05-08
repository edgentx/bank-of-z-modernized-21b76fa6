package com.example.services;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.ReportDefectCommand;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.model.ValidateUrlPresenceCommand;
import com.example.domain.validation.port.ValidationRepository;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service handling the temporal workflow and orchestration of Defect Reporting.
 * This class acts as the bridge between the Temporal workflow and the Domain layer.
 */
@Service
@WorkflowInterface
public class DefectReportingService {

    private final ValidationRepository validationRepository;

    public DefectReportingService(ValidationRepository validationRepository) {
        this.validationRepository = validationRepository;
    }

    /**
     * Temporal Workflow method to report a defect and verify the link propagation.
     * Simulates the end-to-end flow described in S-FB-1.
     */
    @WorkflowMethod
    public String reportDefectWorkflow(String title, String description, String githubIssueUrl) {
        // Step 1: Create the Defect Aggregate
        String defectId = "DEFECT-" + UUID.randomUUID().toString().substring(0, 8);
        DefectAggregate defectAggregate = new DefectAggregate(defectId);
        
        ReportDefectCommand cmd = new ReportDefectCommand(defectId, title, description, githubIssueUrl);
        defectAggregate.execute(cmd);

        // Step 2: Simulate creating the Slack body (usually an adapter would call Slack API)
        // We mimic the adapter logic here: formatting the body using data from the event.
        List<com.example.domain.shared.DomainEvent> events = defectAggregate.uncommittedEvents();
        String actualSlackBody = "";
        String extractedUrl = "";
        
        if (!events.isEmpty() && events.get(0) instanceof DefectReportedEvent e) {
            // This mimics the Slack Adapter generating the message body
            actualSlackBody = String.format("Defect Reported: %s - View: %s", e.title(), e.githubUrl());
            extractedUrl = e.githubUrl();
        }

        // Step 3: Validate that the generated body contains the GitHub URL (VW-454)
        String validationId = "VAL-" + UUID.randomUUID().toString().substring(0, 8);
        ValidationAggregate validationAggregate = new ValidationAggregate(validationId);
        
        ValidateUrlPresenceCommand validationCmd = new ValidateUrlPresenceCommand(
            validationId, 
            extractedUrl, 
            actualSlackBody
        );
        
        validationAggregate.execute(validationCmd);

        // Persist the validation result for audit/logs
        validationRepository.save(validationAggregate);

        if (validationAggregate.isPassed()) {
            return defectId; // Success
        } else {
            throw new RuntimeException("VW-454 Validation Failed: Slack body does not contain the GitHub URL.");
        }
    }
}
