package com.example.domain.vforce360;

import com.example.domain.shared.Command;

public record ReportDefectCmd(String projectId, String description, String severity) implements Command {}