package com.example.domain.vforce360.model;

import com.example.domain.shared.Command;

public record ReportDefectCommand(
    String title,
    String description,
    String severity,
    String component,
    String projectId
) implements Command {}
