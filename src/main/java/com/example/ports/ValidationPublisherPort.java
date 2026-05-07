package com.example.ports;

import com.example.domain.validation.model.DefectReportedEvent;

/**
 * Port for publishing validation related events.
 */
public interface ValidationPublisherPort {
    void publishDefectReported(DefectReportedEvent event);
}
