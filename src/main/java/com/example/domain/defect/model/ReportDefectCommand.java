package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * The defect report will be forwarded to GitHub and a notification sent to Slack.
 */
public record ReportDefectCommand(String defectId, String title, String description) implements Command {}
