package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * This is part of the Reproduction Steps for VW-454.
 */
public record ReportDefectCmd(String defectId, String summary, String gitHubIssueUrl) implements Command {
}
