package com.example.domain.defect.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to report a defect.
 * Immutable record encapsulating the necessary data.
 */
public record ReportDefectCmd(
        String defectId,
        String title,
        String description,
        Map<String, String> metadata
) implements Command {
}