package com.example.domain.defect.model;

import com.example.domain.shared.Command;

public record ReportDefectCmd(String reportId, String githubUrl) implements Command {}
