package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Invoked via Temporal workflow execution.
 */
public record ReportDefectCmd(String defectId) implements Command {}
