package com.example.domain.defect.model;

import com.example.domain.shared.Command;

import java.util.Map;

public record ReportDefectCmd(String projectId, String title, String description, Map<String, String> metadata) implements Command {}