package com.example.domain.vforce360;

import java.time.Instant;
import java.util.Map;

/**
 * Event representing a defect detected by VForce360.
 * S-24 def: Input requires severity, component, summary, and optional context map.
 */
public record DefectReportedEvent(
        String defectId,
        String severity,
        String component,
        String summary,
        Map<String, String> context,
        Instant occurredAt
) {
}