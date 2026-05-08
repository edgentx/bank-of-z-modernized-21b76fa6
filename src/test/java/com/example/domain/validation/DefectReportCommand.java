package com.example.domain.validation;

import com.example.domain.shared.Command;

/**
 * Command representing a defect report request (S-FB-1).
 * Bridges the domain layer with the validation reporting service.
 */
public record DefectReportCommand(String id, String description, String severity) implements Command {}
