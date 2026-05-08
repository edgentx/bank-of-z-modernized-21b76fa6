package com.example.ports;

/**
 * Port interface for interacting with Temporal Workflow context.
 * Abstracts the Temporal SDK to allow for mocking in unit tests.
 */
public interface TemporalWorkflowPort {

    /**
     * Retrieves the current workflow ID from the Temporal context.
     * @return The workflow ID string.
     */
    String getWorkflowId();
}
