package com.example.application;

import com.example.workflows.ReportDefectWorkflow;
import org.springframework.stereotype.Service;

/**
 * Application Service wrapper for the Defect Reporting Workflow.
 */
@Service
public class DefectReportService {

    private final ReportDefectWorkflow workflow;

    public DefectReportService(ReportDefectWorkflow workflow) {
        this.workflow = workflow;
    }

    public String reportDefect(String projectId, String title, String description) {
        return workflow.reportDefect(projectId, title, description);
    }
}