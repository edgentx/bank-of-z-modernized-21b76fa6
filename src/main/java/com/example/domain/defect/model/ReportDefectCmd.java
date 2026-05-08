package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect (e.g. VW-454) via Temporal worker.
 * Includes metadata required to generate the Slack message and GitHub link.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String githubIssueUrl,
    String targetChannel
) implements Command {}
