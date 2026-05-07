package com.example.domain.defect.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to report a defect.
 * Populated by the Temporal worker or test suite.
 */
public record ReportDefectCmd(
        String defectId,
        String title,
        String severity,
        String component,
        String projectId,
        String description,
        Map<String, String> metadata
) implements Command {}
