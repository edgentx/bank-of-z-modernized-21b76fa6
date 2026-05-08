package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Triggered via temporal-worker exec.
 */
public record ReportDefectCmd(
    String projectId,
    String title,
    String description
) implements Command {}
