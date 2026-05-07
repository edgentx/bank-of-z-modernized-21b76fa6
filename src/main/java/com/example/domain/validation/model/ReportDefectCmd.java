package com.example.domain.validation.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to report a defect via the temporal-worker exec.
 * Story: S-FB-1
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String severity,
    String component,
    String projectId
) implements Command {}
