package com.example.domain.validation.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to report a defect.
 * This includes the context required to generate a GitHub issue and a Slack notification.
 */
public record ReportDefectCmd(
        String defectId,
        String severity,
        String component,
        String summary,
        String description,
        Map<String, String> metadata
) implements Command {}
