package com.example.domain.validation.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to report a defect (e.g., VW-454) to VForce360.
 */
public record ReportDefectCmd(
        String defectId,
        String title,
        Map<String, String> metadata
) implements Command {}
