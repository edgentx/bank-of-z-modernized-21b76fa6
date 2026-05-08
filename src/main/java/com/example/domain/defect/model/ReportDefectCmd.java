package com.example.domain.defect.model;

import com.example.domain.shared.Command;

public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String githubUrl // The URL that should be present in the Slack body
) implements Command {}
