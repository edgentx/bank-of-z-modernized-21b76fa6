package com.example.domain.validation.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to report a defect. Includes metadata for verification.
 */
public record ReportDefectCmd(
        String defectId,
        String title,
        String severity,
        Map<String, String> metadata
) implements Command {}
