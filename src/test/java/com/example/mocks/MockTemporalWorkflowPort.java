package com.example.mocks;

import com.example.ports.TemporalWorkflowPort;

/**
 * Mock implementation of TemporalWorkflowPort.
 */
public class MockTemporalWorkflowPort implements TemporalWorkflowPort {
    private String workflowId = "test-workflow-id";

    @Override
    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String id) {
        this.workflowId = id;
    }
}
