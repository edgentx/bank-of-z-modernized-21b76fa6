package com.example.domain.defect;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Used by the temporal workflow to initiate the defect reporting process.
 */
public record ReportDefectCmd(String defectId, String title, String description) implements Command {}