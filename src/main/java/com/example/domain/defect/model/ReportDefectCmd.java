package com.example.domain.defect.model;

import com.example.domain.shared.Command;

public record ReportDefectCmd(
        String defectId,
        String title,
        String description,
        DefectAggregate.Severity severity,
        String component,
        String projectId
) implements Command {}
