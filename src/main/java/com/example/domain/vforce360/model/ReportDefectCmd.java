package com.example.domain.vforce360.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to report a defect.
 * Triggered via temporal-worker exec (Story S-FB-1).
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    Map<String, String> metadata
) implements Command {}
