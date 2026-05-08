package com.example.application;

/**
 * Command object representing a defect report request.
 * Immutable data carrier.
 */
public record DefectReportCommand(
    String defectId,
    String title,
    String severity
) {}
