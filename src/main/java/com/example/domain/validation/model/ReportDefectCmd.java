package com.example.domain.validation.model;

import com.example.domain.shared.Command;

public record ReportDefectCmd(String validationId, String defectId) implements Command {}
