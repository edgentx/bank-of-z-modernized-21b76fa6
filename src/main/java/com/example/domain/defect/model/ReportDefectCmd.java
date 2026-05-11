package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect. 
 * Expected to result in a GitHub issue and a Slack notification containing the URL.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String severity
) implements Command {}
