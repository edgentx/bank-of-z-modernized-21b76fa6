package com.example.domain.notification.model;

import com.example.domain.shared.Command;

public record ReportDefectCommand(
    String defectId,
    String title,
    String description,
    String severity
) implements Command {}