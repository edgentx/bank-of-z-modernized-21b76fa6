package com.example.domain.validation;

import com.example.domain.shared.SlackMessageValidator;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface DefectReportWorkflow {

    @WorkflowMethod
    String reportDefectWorkflow(String defectId, String title, String severity);

    // Default static method for helper logic if needed, or keep interface clean
}
