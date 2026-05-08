package com.example.domain.vforce360.model;

import com.example.domain.shared.Command;

public record ReportDefectCmd(
    String aggregateId,
    String title,
    String severity,
    String component
) implements Command {}
