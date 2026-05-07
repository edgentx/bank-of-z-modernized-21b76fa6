package com.example.domain.verification.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to report a defect from the VForce360 diagnostic conversation.
 */
public record ReportDefectCommand(
        String defectId,
        String title,
        String description,
        String severity,
        Map<String, String> metadata
) implements Command {}
