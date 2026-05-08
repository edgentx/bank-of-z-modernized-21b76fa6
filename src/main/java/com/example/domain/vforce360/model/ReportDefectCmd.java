package com.example.domain.vforce360.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect via VForce360.
 * Context: Story S-FB-1, S-17
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String githubUrl
) implements Command {}
