package com.example.infrastructure.workflow;

import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import org.springframework.stereotype.Component;

@WorkflowInterface
public interface DefectReportingWorkflow {
    @WorkflowMethod
    String reportDefect(String title, String description);
}

@WorkflowImpl(taskQueue = "DEFECT_TASK_QUEUE")
@Component
public class DefectReportingWorkflowImpl implements DefectReportingWorkflow {
    @Override
    public String reportDefect(String title, String description) {
        // Placeholder
        return "url";
    }
}
