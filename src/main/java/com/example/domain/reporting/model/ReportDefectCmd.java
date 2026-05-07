package com.example.domain.reporting.model;

import com.example.domain.shared.Command;
import java.util.Map;

public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    Map<String, String> metadata
) implements Command {}
