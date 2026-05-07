package com.example.domain.defect;

import com.example.domain.shared.Command;

/**
 * Command to report a defect to the VForce360 system.
 */
public record ReportDefectCommand(
        String projectId,
        String title,
        String description
) implements Command {}
