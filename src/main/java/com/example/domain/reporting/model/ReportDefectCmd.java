package com.example.domain.reporting.model;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to report a defect discovered in the VForce360 system.
 * Part of S-FB-1: Fix for VW-454.
 */
public record ReportDefectCmd(
    String defectId,
    String description,
    String severity,
    Instant occurredAt
) implements Command {}
