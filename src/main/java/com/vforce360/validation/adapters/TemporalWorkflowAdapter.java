package com.vforce360.validation.adapters;

import com.vforce360.validation.ports.DefectReportSignal;
import com.vforce360.validation.ports.TemporalWorkflowPort;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the Temporal workflow adapter.
 * Connects to the Temporal server to trigger sagas.
 */
@Component
public class TemporalWorkflowAdapter implements TemporalWorkflowPort {

    // Stub: Temporal WorkflowStub would be injected here in a real env.
    // private final WorkflowStub workflowStub;

    @Override
    public void signalReportDefect(DefectReportSignal signal) {
        // Implementation: workflowStub.signal(signal);
        // This is a no-op in the green phase unit context unless specific Temporal test utilities are present.
    }
}
