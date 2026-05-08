package com.example.domain.vforce360;

import com.example.domain.shared.Command;

/**
 * Command to report a defect (VForce360 diagnostic).
 */
public record ReportDefectCommand(String issueId, String title, String description) implements Command {}
