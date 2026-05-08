package com.vforce360.validation.ports;

import java.io.Serializable;
import java.time.Instant;

/**
 * Signal object passed to Temporal workflows.
 */
public class DefectReportSignal implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String sourceSystem;
    private final String payload;
    private final Instant timestamp;

    public DefectReportSignal(String sourceSystem, String payload) {
        this.sourceSystem = sourceSystem;
        this.payload = payload;
        this.timestamp = Instant.now();
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public String getPayload() {
        return payload;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
