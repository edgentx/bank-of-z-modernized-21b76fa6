package com.example.workflow;

import com.example.domain.validation.model.ValidationAggregate;
import io.temporal.spring.boot.WorkflowImpl;

@WorkflowImpl
public class DefectReportingWorkflowImpl {

    private final ValidationAggregate aggregate;

    public DefectReportingWorkflowImpl() {
        this.aggregate = new ValidationAggregate("dummy-id");
    }

    public void execute(String reportId) {
        // Implementation stub to satisfy compilation
    }
}