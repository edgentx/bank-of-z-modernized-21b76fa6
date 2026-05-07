package com.example.domain.validation;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Driven by Story S-FB-1 (Validating VW-454).
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String projectId,
    String severity
) implements Command {}
