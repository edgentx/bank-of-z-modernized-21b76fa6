package com.example.domain.diagnostic;

import com.example.domain.shared.Command;

public record ReportDefectCmd(
    String issueId,
    String title,
    String description,
    String severity,
    String component
) implements Command {}
