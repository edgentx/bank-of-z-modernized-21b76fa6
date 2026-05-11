package com.example.domain.defect.model;

import com.example.domain.shared.Command;

public record ReportDefectCommand(String defectId, String description, String githubIssueUrl) implements Command {
    public ReportDefectCommand {
        if (defectId == null || defectId.isBlank()) throw new IllegalArgumentException("defectId required");
    }
}