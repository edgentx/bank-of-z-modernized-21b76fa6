package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 */
public record ReportDefectCommand(
    String defectId,
    String issueId,     // e.g., "VW-454"
    String githubUrl,   // e.g., "https://github.com/..."
    String summary
) implements Command {}
