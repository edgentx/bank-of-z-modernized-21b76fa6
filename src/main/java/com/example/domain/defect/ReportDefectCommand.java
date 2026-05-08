package com.example.domain.defect;

import com.example.domain.shared.Command;

/**
 * Command to report a defect discovered in the system.
 * Triggered by the Temporal workflow execution.
 */
public record ReportDefectCommand(String defectId, String targetChannel) implements Command {
}
