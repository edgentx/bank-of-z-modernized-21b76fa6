package com.example.workflow;

import io.temporal.workflow.Workflow;

public class DefectWorkflowImpl implements DefectWorkflow {

    private final DefectActivities activities = Workflow.newActivityStub(DefectActivities.class);

    @Override
    public String executeReportDefect(String projectId, String title, String description) {
        // Map inputs to Command
        var cmd = new com.example.domain.defect.model.ReportDefectCommand(
                projectId,
                title,
                description,
                com.example.domain.defect.model.ReportDefectCommand.Severity.LOW,
                "validation",
                null
        );

        // Execute activity
        return activities.reportDefect(cmd);
    }
}
