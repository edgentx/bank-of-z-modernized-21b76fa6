package com.example.application;

import java.io.Serializable;

/**
 * Command object representing a defect report request.
 * Must be Serializable to pass through Temporal workflows.
 */
public record DefectReportCommand(
    String defectId,
    String title,
    String severity
) implements Serializable {}
