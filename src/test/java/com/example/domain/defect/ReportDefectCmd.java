package com.example.domain.defect;

import com.example.domain.shared.Command;

/**
 * Command to report a defect (VW-454).
 * Triggers GitHub issue creation and Slack notification.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String severity,
    String description
) implements Command {}
