package com.example.domain.defect.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to report a defect (e.g., triggered via Temporal).
 * Context: Story VW-454 / S-FB-1.
 */
public record ReportDefectCmd(
        String defectId,
        String title,
        String description,
        String projectId,
        Map<String, String> metadata
) implements Command {
}