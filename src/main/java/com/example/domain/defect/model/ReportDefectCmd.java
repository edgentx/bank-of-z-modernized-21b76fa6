package com.example.domain.defect.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to report a defect initiated via VForce360 PM diagnostic conversation.
 * Maps to Story S-FB-1 (VW-454).
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String severity,
    String component,
    String projectId,
    Map<String, String> metadata
) implements Command {}