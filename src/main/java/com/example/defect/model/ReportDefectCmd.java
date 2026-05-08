package com.example.defect.model;

import com.example.domain.shared.Command;

public record ReportDefectCmd(
        String defectId,
        String title,
        String description,
        String severity,
        String stackTrace
) implements Command {}
