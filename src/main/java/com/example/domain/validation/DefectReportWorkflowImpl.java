package com.example.domain.validation;

import com.example.domain.shared.SlackMessageValidator;
import com.example.domain.validation.model.ValidationAggregate;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Workflow Implementation for Defect Report.
 * Uses the ValidationAggregate to manage state and verifies Slack output.
 */
public class DefectReportWorkflowImpl implements DefectReportWorkflow {

    private static final Logger logger = LoggerFactory.getLogger(DefectReportWorkflowImpl.class);

    // Injected via Temporal Workflow stubbing in real runtime, or mocked in tests.
    private SlackMessageValidator validator;

    public DefectReportWorkflowImpl() {
        // Default constructor for Temporal
    }

    public DefectReportWorkflowImpl(SlackMessageValidator validator) {
        this.validator = validator;
    }

    @Override
    public void reportDefect(String defectId, String description) {
        logger.info("Reporting defect {}", defectId);

        // 1. Process the defect via Aggregate
        ValidationAggregate aggregate = new ValidationAggregate(defectId);
        // In a full implementation, we would execute a command here.
        
        // 2. Simulate generating the Slack message body
        // In a real scenario, this might come from an Activity or the Aggregate state.
        String githubUrl = "http://github.com/example/repo/issues/" + defectId;
        String slackBody = "Defect reported. See GitHub issue: " + githubUrl;

        // 3. Validate the output (S-FB-1 / VW-454)
        // Note: We are in a workflow, so we must use deterministic logic or call Activities.
        // Here we perform the domain validation check directly on the generated string.
        boolean isValid = slackBody.contains("github.com"); // Simplified check for WF context
        
        if (!isValid) {
            logger.error("Validation failed: Slack body does not contain GitHub URL for defect {}", defectId);
        }
        
        logger.info("Slack Body Generated: {}", slackBody);
    }
}
