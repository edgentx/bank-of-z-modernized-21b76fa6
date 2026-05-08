package com.example.domain.validation.model;

import com.example.domain.shared.Command;

public record ReportDefectCmd(
    String defectId,
    String description,
    String severity,
    String component
) implements Command {}
