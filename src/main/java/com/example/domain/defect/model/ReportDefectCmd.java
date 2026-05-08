package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Used to trigger the defect reporting workflow which eventually posts to Slack.
 */
public record ReportDefectCmd(String defectId, String issueId, String description) implements Command {}
