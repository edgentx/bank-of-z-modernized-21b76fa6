package com.example.application;

/**
 * Input command for reporting a defect.
 * Wraps the parameters sent by the Temporal workflow or API.
 */
public record ReportDefectCommand(
    String projectId,
    String defectId,
    String title,
    String githubUrl,
    String severity
) {}
