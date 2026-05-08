package com.example.workflow;

import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.repository.ValidationRepository;
import com.example.workflows.ReportDefectActivity;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@WorkflowImpl(taskQueue = "DEFECT_REPORTING_TASK_QUEUE")
public class DefectReportingWorkflowImpl implements DefectReportingWorkflow {

    // Workflow fields must be serializable by Temporal (usually Interfaces).
    // We use lazy initialization via Workflow.newActivityStub or injection if supported.
    // The test environment registers activities manually, but we hold the interface ref here.
    private final ReportDefectActivity activities = Workflow.newActivityStub(ReportDefectActivity.class);

    // The repository is NOT used directly in the workflow path for this specific defect fix,
    // as the Temporal worker requires a no-arg constructor or specific serialization.
    // However, in a full CQRS implementation, the command side would eventually persist.
    // For this specific S-FB-1 fix, we focus on the Activity execution.

    public DefectReportingWorkflowImpl() {
    }

    @Override
    public String reportDefect(String validationId, String message, String githubUrl) {
        // Business logic validation would normally happen here on a reloaded aggregate.
        // For the scope of VW-454 (Slack body validation), we trigger the activity chain.
        
        // Execute the activity to report to Slack
        return activities.reportDefectToSlack(message, githubUrl);
    }
}
