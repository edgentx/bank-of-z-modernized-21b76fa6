package com.example.domain.validation.model;

import com.example.domain.shared.Command;

import java.util.Objects;

public record ReportDefectCmd(String defectId, String description) implements Command {
    public ReportDefectCmd {
        Objects.requireNonNull(defectId, "defectId cannot be null");
        Objects.requireNonNull(description, "description cannot be null");
    }
}
