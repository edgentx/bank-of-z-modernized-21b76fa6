package com.example.domain.validation;

import com.example.domain.shared.Command;

public record ReportDefectCmd(String defectId, String initialUrl) implements Command {}
