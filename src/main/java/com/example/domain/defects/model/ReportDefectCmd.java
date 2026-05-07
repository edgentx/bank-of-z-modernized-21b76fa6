package com.example.domain.defects.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to report a defect.
 * This is likely part of a workflow input, but treated here as a command object
 * to pass into the validation/service layer.
 */
public record ReportDefectCmd(
        String defectId,
        String title,
        String description,
        String severity,
        Map<String, String> metadata
) implements Command {
}
