package com.example.domain.defect.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to report a defect detected in VForce360.
 * Used by temporal-worker exec to trigger Slack notifications.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String severity,
    Map<String, String> metadata // e.g., "story_id" -> "S-FB-1"
) implements Command {}
