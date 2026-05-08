package com.example.domain.defect.model;

/**
 * Command object representing a request to report a defect.
 * Used as input to the DefectReportingService.
 */
public record ReportDefectCommand(
    String projectId,
    String title,
    String description,
    String githubUrl
) {}
