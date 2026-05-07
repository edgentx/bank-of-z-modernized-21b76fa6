package com.example.domain.validation;

import com.example.domain.shared.Command;

public record ReportDefectCommand(
    String defectId,
    String title,
    String description
) implements Command {}
