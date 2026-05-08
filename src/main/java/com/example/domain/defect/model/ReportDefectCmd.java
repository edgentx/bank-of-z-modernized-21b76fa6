package com.example.domain.defect.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to report a defect (e.g., to GitHub and Slack).
 * Used by temporal-worker exec.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String githubUrl,
    Map<String, String> metadata
) implements Command {}
