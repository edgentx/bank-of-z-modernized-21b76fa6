package com.example.domain.defect;

import com.example.domain.shared.Command;

/**
 * Command to report a defect to the VForce360 system.
 * Expected to trigger a Slack notification containing the GitHub issue URL.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String githubUrl
) implements Command {
}
