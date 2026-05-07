package com.example.domain.notification.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Triggered by temporal-worker or diagnostic processes.
 */
public record ReportDefectCmd(String defectId) implements Command {}
