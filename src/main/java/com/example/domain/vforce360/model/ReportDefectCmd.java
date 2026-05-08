package com.example.domain.vforce360.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect to VForce360.
 */
public record ReportDefectCmd(String defectId, String githubUrl, String slackChannel) implements Command {}
