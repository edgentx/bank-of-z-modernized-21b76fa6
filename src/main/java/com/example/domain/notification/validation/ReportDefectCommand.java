package com.example.domain.notification.validation;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Triggered via temporal-worker exec (or direct API for testing).
 */
public record ReportDefectCommand(String issueId, String description) implements Command {}
