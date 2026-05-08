package com.example.domain.diagnostic;

import com.example.domain.shared.Command;

/**
 * Command to report a defect discovered in the system.
 */
public record ReportDefectCmd(
    String issueId,
    String title,
    String description,
    String severity,
    String component
) implements Command {}
