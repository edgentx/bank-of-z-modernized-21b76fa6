package com.example.domain.reporting.model;

import com.example.domain.shared.Command;

public record ReportDefectCmd(
    String defectId,
    String title,
    String description
) implements Command {}
