package com.example.domain.vforce360.model;

import com.example.domain.shared.Command;
import java.util.Map;

public record ReportDefectCommand(
    String defectId,
    String title,
    String description,
    String severity,
    Map<String, String> metadata
) implements Command {}
