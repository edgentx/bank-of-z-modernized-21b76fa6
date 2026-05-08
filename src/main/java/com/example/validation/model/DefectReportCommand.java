package com.example.validation.model;

import com.example.domain.shared.Command;

/**
 * Command representing a defect report request.
 * This is the input object for the workflow triggering the defect report.
 */
public record DefectReportCommand(
    String defectId,
    String projectKey,
    String description
) implements Command {}
