package com.example.domain.defect;

import com.example.domain.shared.Command;

/**
 * Command to report a defect observed in production or diagnostics.
 * Corresponds to the temporal-worker trigger "_report_defect".
 */
public record ReportDefectCommand(
    String defectId,
    String title,
    String description,
    String source // e.g., "VForce360 PM diagnostic"
) implements Command {}