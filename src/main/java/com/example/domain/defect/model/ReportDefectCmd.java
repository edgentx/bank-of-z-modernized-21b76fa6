package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * This serves as the input for the defect reporting workflow.
 */
public record ReportDefectCmd(String title, String description) implements Command {}
