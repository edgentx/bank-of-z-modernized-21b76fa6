package com.example.domain.defect.model;

import com.example.domain.shared.Command;
import java.util.Map;

public record ReportDefectCmd(
    String defectId,
    String title,
    String severity,
    String component,
    String projectId,
    Map<String, String> metadata
) implements Command {}