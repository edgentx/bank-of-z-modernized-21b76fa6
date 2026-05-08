package com.example.domain.validation;

import io.temporal.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefectReportWorkflowImpl implements DefectReportWorkflow {

    private static final Logger log = LoggerFactory.getLogger(DefectReportWorkflowImpl.class);

    @Override
    public String reportDefectWorkflow(String defectId, String title, String severity) {
        log.info("Reporting defect {} via Temporal Workflow", defectId);
        // Workflow logic stub - main logic is in Activities
        return "Reported: " + defectId;
    }
}
