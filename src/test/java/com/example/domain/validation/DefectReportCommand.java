package com.example.domain.validation;

import com.example.domain.shared.Command;

/**
 * Command representing a defect report request (S-FB-1).
 */
public record DefectReportCommand(String id, String description, String severity) implements Command {}