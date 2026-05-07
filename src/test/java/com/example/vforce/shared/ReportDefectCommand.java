package com.example.vforce.shared;

/**
 * Command object representing a request to report a defect.
 * Typically sourced from the Temporal workflow or PM diagnostic conversation.
 */
public record ReportDefectCommand(String summary) {}
