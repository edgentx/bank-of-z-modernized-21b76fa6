package com.example.domain.validation;

import com.example.domain.shared.Command;
import java.util.Map;

public record ReportDefectCmd(String defectId, String title, String severity, String component, String projectId, Map<String, String> context) implements Command {
}
