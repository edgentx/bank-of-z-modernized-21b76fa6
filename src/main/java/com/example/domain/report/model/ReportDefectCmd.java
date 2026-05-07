package com.example.domain.report.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate the defect reporting workflow.
 * Part of the Report Aggregate context.
 */
public record ReportDefectCmd(String issueId, String description) implements Command {}
