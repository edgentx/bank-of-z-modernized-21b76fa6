package com.example.domain.vforce360.model;

import com.example.domain.shared.Command;

public record ReportDefectCmd(
    String defectId,
    String project,
    String severity,
    String description
) implements Command {
}