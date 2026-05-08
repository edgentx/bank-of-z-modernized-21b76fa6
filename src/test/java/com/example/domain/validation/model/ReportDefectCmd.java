package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect. 
 * Part of the validation layer fix for S-FB-1.
 */
public record ReportDefectCmd(String summary, String description) implements Command {}
