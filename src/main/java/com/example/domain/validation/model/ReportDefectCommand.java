package com.example.domain.validation.model;

import com.example.domain.shared.Command;

public record ReportDefectCommand(String defectId, String title, String description) implements Command {}
