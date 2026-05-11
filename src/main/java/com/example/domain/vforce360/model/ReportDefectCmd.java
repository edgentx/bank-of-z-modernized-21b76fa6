package com.example.domain.vforce360.model;

import com.example.domain.shared.Command;
import java.util.Map;

public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String githubUrl,
    Map<String, Object> metadata
) implements Command {}
