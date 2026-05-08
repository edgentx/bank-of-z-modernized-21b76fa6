package com.example.domain.vforce.model;

/**
 * Command to report a defect.
 */
public record ReportDefectCommand(String summary, String severity, String description) {}
