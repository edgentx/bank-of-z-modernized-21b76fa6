package com.example.domain.validation;

import com.example.domain.shared.SlackMessageValidator;
import com.example.domain.validation.model.ValidationAggregate;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow for reporting defects.
 * Orchestrates the validation and Slack notification logic.
 */
@WorkflowInterface
public interface DefectReportWorkflow {

    @WorkflowMethod
    void reportDefect(String defectId, String description);

}
