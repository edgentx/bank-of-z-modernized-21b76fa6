package com.example.adapters;

import com.example.application.DefectReportingActivity;
import com.example.domain.verification.model.ReportDefectCommand;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Temporal Workflow definition for reporting defects.
 * Coordinates the execution of the Activity.
 */
@WorkflowInterface
public interface TemporalReportDefectWorkflow {

    @WorkflowMethod
    void executeReportDefect(ReportDefectCommand command);

    /**
     * Workflow Implementation.
     */
    class WorkflowImpl implements TemporalReportDefectWorkflow {

        private static final Logger log = LoggerFactory.getLogger(WorkflowImpl.class);

        private final DefectReportingActivity activity;
        // State kept during workflow execution
        private final List<String> statusMessages = new ArrayList<>();

        // Workflow constructor injected by Temporal
        public WorkflowImpl(DefectReportingActivity activity) {
            this.activity = activity;
        }

        @Override
        public void executeReportDefect(ReportDefectCommand command) {
            log.info("Starting workflow for defect: {}", command.defectId());
            statusMessages.add("Started");

            // Execute the activity which calls VerificationService
            activity.reportDefect(command);

            statusMessages.add("Completed");
            log.info("Workflow completed for defect: {}", command.defectId());
        }

        @SignalMethod
        void addStatus(String msg) {
            statusMessages.add(msg);
        }
    }
}
