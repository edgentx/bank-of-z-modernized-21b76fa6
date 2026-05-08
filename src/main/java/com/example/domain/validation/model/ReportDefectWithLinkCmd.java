package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Internal command used to carry the generated GitHub link to the Slack adapter.
 */
public record ReportDefectWithLinkCmd(
    String defectId,
    String title,
    String description,
    String githubUrl
) implements Command {}
