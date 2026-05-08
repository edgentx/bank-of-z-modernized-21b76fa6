package com.example.domain.validation.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to report a defect via the temporal-worker.
 * Part of VW-454 fix: ensuring GitHub URL is propagated to Slack body.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String githubUrl,
    Map<String, Object> metadata
) implements Command {}
