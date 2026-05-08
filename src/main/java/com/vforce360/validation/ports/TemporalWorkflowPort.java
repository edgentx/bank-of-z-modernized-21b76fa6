package com.vforce360.validation.ports;

/**
 * Port interface for Temporal Workflow interactions.
 * Used to trigger durable workflows (e.g., account opening sagas, defect reporting).
 */
public interface TemporalWorkflowPort {
    
    /**
     * Signals a defect report workflow to start.
     * @param signal The payload containing defect details.
     */
    void signalReportDefect(DefectReportSignal signal);
}
