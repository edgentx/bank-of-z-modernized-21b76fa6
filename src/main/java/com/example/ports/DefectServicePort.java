package com.example.ports;

import com.example.domain.shared.Command;

/**
 * Port interface for Defect Reporting Service.
 * This abstraction allows us to mock the service behavior in tests
 * without connecting to the real Temporal workflow engine.
 */
public interface DefectServicePort {
    void reportDefect(ReportDefectCommand cmd);

    /**
     * Command DTO for defect reporting.
     */
    record ReportDefectCommand(
        String defectId,
        String title,
        String severity,
        String component
    ) implements Command {}
}
