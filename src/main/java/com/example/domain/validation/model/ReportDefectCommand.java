package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a defect report.
 * Bridges the Temporal workflow input to the Domain Service.
 */
public record ReportDefectCommand(
    String id,
    String title,
    String description
) implements Command {}
