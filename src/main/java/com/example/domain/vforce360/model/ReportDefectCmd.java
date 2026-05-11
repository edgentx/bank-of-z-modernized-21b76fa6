package com.example.domain.vforce360.model;

import com.example.domain.shared.Command;

public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String projectId
) implements Command {}
