package com.example.domain.defect;

import com.example.domain.shared.Command;

public record ReportDefectCommand(String id, String title, String githubUrl) implements Command {}
