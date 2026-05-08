package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Part of the reconciliation/validation domain.
 */
public record ReportDefectCmd(String defectId, String title, String description) implements Command {
}
