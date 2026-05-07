package com.example.vforce.shared;

import com.example.domain.shared.validation.ValidationViolation;
import java.util.List;

/**
 * Command to report a defect.
 * Used by the temporal workflow and activity.
 */
public record ReportDefectCommand(
        String title,
        String description,
        List<ValidationViolation> violations
) {
    public ReportDefectCommand {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title required");
        // Description and violations can be optional depending on severity, but required for this context
    }
}