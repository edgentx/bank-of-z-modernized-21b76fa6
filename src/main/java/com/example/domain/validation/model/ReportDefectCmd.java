package com.example.domain.validation.model;

import com.example.domain.shared.Command;

public record ReportDefectCmd(String defectId, String title, String githubUrl) implements Command {}
