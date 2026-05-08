package com.example.domain.validation;

import com.example.ports.VForce360NotificationPort;

/**
 * Placeholder implementation class for the Orchestrator/Workflow.
 * In a real Temporal setup, this would be a Workflow Interface/Impl.
 * We create this simple class to simulate the worker triggering the defect report.
 * 
 * This represents the 'System Under Test' boundary.
 */
public class ReportDefectWorkflowOrchestrator {

    private final VForce360NotificationPort notificationPort;

    public ReportDefectWorkflowOrchestrator(VForce360NotificationPort notificationPort) {
        this.notificationPort = notificationPort;
    }

    public void reportDefect(String defectId) {
        // This is a STUB implementation representing the current broken state.
        // In the Red phase, this code deliberately omits the URL to cause the test to fail,
        // or this class doesn't exist yet.
        // For the purpose of this Red Phase test file generation, we include it 
        // so the code compiles and the test fails legitimately on the assertion logic.
        
        String body = "Defect Reported: " + defectId; 
        
        // Intentionally missing the URL logic to simulate the defect.
        // String url = "https://github.com/example/bank-of-z/issues/" + defectId.split("-")[1];
        // body += "\n" + url;

        notificationPort.reportDefect(defectId, body);
    }
}
