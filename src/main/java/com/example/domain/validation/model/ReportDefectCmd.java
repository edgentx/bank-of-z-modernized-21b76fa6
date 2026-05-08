package com.example.domain.validation.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to report a defect.
 * Contains metadata required to generate the GitHub issue and Slack notification.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String severity,
    Map<String, String> metadata
) implements Command {
    // Command definition
}
