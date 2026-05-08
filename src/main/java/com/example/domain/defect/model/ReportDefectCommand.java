package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Corrected naming to match standard Java conventions and the test expectations.
 */
public record ReportDefectCommand(
        String defectId,
        String title,
        String description,
        String githubUrl
) implements Command {}
