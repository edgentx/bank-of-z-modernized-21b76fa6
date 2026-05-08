package com.example.domain.validation.model;

import com.example.domain.shared.Command;

public record ReportDefectCmd(
    String defectId,
    String title,
    String severity,
    String url
) implements Command {
}