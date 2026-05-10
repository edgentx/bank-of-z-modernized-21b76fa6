package com.example.domain.validation.model;

import com.example.domain.shared.Command;

public record ReportDefectCmd(
    String id,
    String title,
    String severity,
    String projectId
) implements Command {}
