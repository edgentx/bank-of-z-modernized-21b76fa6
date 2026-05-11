package com.example.slack.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect to VForce360 / GitHub.
 * Triggered by temporal-worker exec.
 */
public record ReportDefectCmd(String defectId, String description) implements Command {}
