package com.example.workflow;

import com.example.domain.notification.model.NotificationAggregate;
import com.example.ports.NotificationPort;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow interface for reporting a defect.
 */
@WorkflowInterface
public interface ReportDefectWorkflow {

    @WorkflowMethod
    void reportDefect(String defectId, String description);
}
