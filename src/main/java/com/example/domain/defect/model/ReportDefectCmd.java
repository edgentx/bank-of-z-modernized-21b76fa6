package com.example.domain.defect.model;

import com.example.domain.shared.Command;
import java.util.Optional;

public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    DefectAggregate.Severity severity,
    String component,
    String githubIssueUrl
) implements Command {}