package com.example.domain.vforce;

import com.example.domain.shared.Command;

/**
 * Command to report a defect to VForce360.
 * Represents the payload entering the system via the Temporal worker.
 */
public record ReportDefectCommand(String defectId, String summary) implements Command {}
