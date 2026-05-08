package com.example.domain.vforce360.model;

import com.example.domain.shared.Command;

public record ReportDefectCmd(
    String issueId,
    String title,
    String severity
) implements Command {}
