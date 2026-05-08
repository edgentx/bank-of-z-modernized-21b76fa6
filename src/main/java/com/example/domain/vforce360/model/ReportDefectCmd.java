package com.example.domain.vforce360.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect (e.g., VW-454) via VForce360 diagnostics.
 */
public record ReportDefectCmd(String defectId, String title) implements Command {}
