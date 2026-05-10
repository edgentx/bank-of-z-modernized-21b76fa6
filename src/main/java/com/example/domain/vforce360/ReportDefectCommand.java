package com.example.domain.vforce360;

import com.example.domain.shared.Command;

/**
 * Command to report a defect raised via VForce360 PM diagnostics.
 */
public record ReportDefectCommand(
    String aggregateId,
    String title,
    String severity,
    String component,
    String description
) implements Command {}
