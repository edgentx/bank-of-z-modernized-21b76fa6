package com.example.domain.defect.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to report a defect via the VForce360 temporal workflow.
 * Corresponds to the story: Fix: Validating VW-454.
 */
public record ReportDefectCmd(
        String defectId,
        String title,
        String description,
        Map<String, String> metadata
) implements Command {
}