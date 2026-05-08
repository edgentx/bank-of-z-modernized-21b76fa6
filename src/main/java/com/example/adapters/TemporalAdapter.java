package com.example.adapters;

import com.example.ports.TemporalWorkflowPort;
import io.temporal.workflow.Workflow;

/**
 * Adapter implementation for Temporal Workflow context.
 * Bridges the application ports to the Temporal SDK.
 */
public class TemporalAdapter implements TemporalWorkflowPort {

    /**
     * Retrieves the current workflow ID from the Temporal context.
     * Note: This must be called within a Workflow context to work.
     */
    @Override
    public String getWorkflowId() {
        try {
            return Workflow.getInfo().getWorkflowId();
        } catch (Exception e) {
            // Fallback or rethrow depending on strictness of context requirements
            throw new RuntimeException("Unable to retrieve Temporal Workflow ID. Are we in a Workflow context?", e);
        }
    }
}
