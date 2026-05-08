package com.example.model;

import java.util.Optional;

/**
 * Domain object representing a defect report triggered by VForce360.
 */
public record DefectReport(
    String defectId,
    String title,
    String githubUrl
) {}
