package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect from VForce360.
 * This is the trigger for the VW-454 scenario.
 */
public record ReportDefectCmd(String defectId, String description, String severity) implements Command {}
