package com.example.domain.vforce360.model;

import com.example.domain.shared.Command;

public record ReportDefectCommand(
    String defectId,
    String description,
    String severity
) implements Command {}