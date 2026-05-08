package com.example.domain.validation.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to report a defect.
 * Context: S-FB-1 (Validating VW-454)
 */
public record ReportDefectCmd(
        String defectId,
        String title,
        String description,
        Map<String, String> metadata // e.g. source="VForce360 PM diagnostic"
) implements Command {}