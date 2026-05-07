package com.example.domain.vforce.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Ideally used by Temporal workflows to trigger the reporting flow.
 */
public record ReportDefectCmd(String summary, String description) implements Command {}
