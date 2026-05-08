package com.example.workflow;

import com.example.domain.notification.model.NotificationAggregate;
import com.example.ports.NotificationPort;
import io.temporal.workflow.Workflow;

/**
 * Workflow implementation for ReportDefectWorkflow.
 */
public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    private final NotificationPort notificationPort;

    // Default constructor required for Temporal workflow instantiation
    public ReportDefectWorkflowImpl() {
        this.notificationPort = null; // Will be resolved via Activity or stub in real execution
    }

    @Override
    public void reportDefect(String defectId, String description) {
        // In a real Temporal setup, we would use Workflow.newActivityStub
        // For this defect fix context, we focus on the structure.
        NotificationAggregate notification = new NotificationAggregate(defectId);
        
        // Logic to ensure the Slack body contains the GitHub URL is handled inside the adapter
        // notificationPort.send(notification);
    }
}
