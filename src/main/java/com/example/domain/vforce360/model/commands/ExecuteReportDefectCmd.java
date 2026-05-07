package com.example.domain.vforce360.model.commands;

import com.example.domain.shared.Command;

public record ExecuteReportDefectCmd(
    String projectId,
    String validationId,
    String defectId
) implements Command {}