package com.example.ports;

/**
 * Port for triggering the defect reporting workflow via Temporal.
 */
public interface TemporalDefectPort {
    void triggerDefectReport(String defectId, String description);
}
