package com.example.domain.vforce.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to report a defect detected by the VForce360 PM diagnostic system.
 */
public record ReportDefectCmd(
    String issueId,
    String severity,
    String description,
    Map<String, Object> metadata
) implements Command {}
