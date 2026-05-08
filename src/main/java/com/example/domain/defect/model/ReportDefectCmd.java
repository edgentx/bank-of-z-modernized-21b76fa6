package com.example.domain.defect.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to report a defect (VW-454, etc.).
 * Contains context information including the GitHub URL.
 */
public record ReportDefectCmd(
        String defectId,
        String title,
        String description,
        String githubUrl,
        Map<String, Object> metadata
) implements Command {}
