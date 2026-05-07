package com.example.domain.defect.model;

import com.example.domain.shared.Command;

import java.util.Map;
import java.util.Objects;

/**
 * Command to report a defect initiated via Temporal worker.
 * Contains the payload necessary to generate the Slack message.
 */
public record ReportDefectCmd(
        String defectId,
        String title,
        String description,
        Map<String, String> metadata
) implements Command {
    public ReportDefectCmd {
        Objects.requireNonNull(defectId, "defectId is required");
        Objects.requireNonNull(title, "title is required");
    }
}
