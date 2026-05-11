package com.example.domain.validation.model;

import com.example.domain.shared.Command;
import java.util.Map;

/** Command to report a defect (e.g., from Temporal workflow). */
public record ReportDefectCmd(
    String defectId,
    String title,
    String severity,
    String description,
    Map<String, String> metadata
) implements Command {}
