package com.example.domain.report.model;

import com.example.domain.shared.Command;

import java.util.Map;

public record ReportDefectCmd(String defectId, String title, String severity, Map<String, Object> metadata) implements Command {
    @Override
    public String type() {
        return "ReportDefect";
    }

    @Override
    public Object payload() {
        return metadata;
    }
}
