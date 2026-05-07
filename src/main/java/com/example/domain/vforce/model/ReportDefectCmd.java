package com.example.domain.vforce.model;

import com.example.domain.shared.Command;

/** Command to report a defect via VForce360. */
public record ReportDefectCmd(
    String defectId,
    String title,
    String severity,
    String component
) implements Command {}
