package com.example.domain.vforce360.model;

import com.example.domain.shared.Command;

public record ReportDefectCmd(
    String title,
    String body,
    String project,
    String severity
) implements Command {}
