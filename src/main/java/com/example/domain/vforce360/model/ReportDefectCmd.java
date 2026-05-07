package com.example.domain.vforce360.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to report a defect discovered during execution.
 * Part of the VForce360 diagnostic conversation workflow.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    Map<String, String> metadata
) implements Command {}