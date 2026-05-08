package com.example.domain.vforce360;

import io.temporal.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Workflow implementation for defect reporting.
 * Connects the workflow definition to the activity.
 */
public class DefectReportWorkflowImpl implements DefectReportWorkflow {

    private static final Logger log = LoggerFactory.getLogger(DefectReportWorkflowImpl.class);

    // Activity stub injected by Temporal
    private final DefectReportActivity activity = Workflow.newActivityStub(DefectReportActivity.class);

    @Override
    public void reportDefect(String defectId) {
        log.info("Starting defect report workflow for ID: {}", defectId);
        activity.reportDefect(defectId);
        log.info("Completed defect report workflow for ID: {}", defectId);
    }
}