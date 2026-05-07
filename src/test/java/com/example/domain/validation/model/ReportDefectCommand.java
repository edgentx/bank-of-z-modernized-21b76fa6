package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Triggers creation of a GitHub issue and a Slack notification.
 */
public record ReportDefectCommand(String title, String description) implements Command {}