package com.example.domain.defect;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Used to trigger the defect reporting workflow.
 */
public record ReportDefectCommand(String defectId, String summary) implements Command {}
