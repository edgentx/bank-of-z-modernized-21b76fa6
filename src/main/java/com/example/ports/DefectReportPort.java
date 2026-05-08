package com.example.ports;

import com.example.domain.shared.ReportDefectCmd;

/**
 * Port interface for handling defect report commands.
 * This abstraction allows the domain layer to trigger reporting logic
 * without depending directly on the Temporal workflow implementation.
 */
public interface DefectReportPort {
    void handleDefectReport(ReportDefectCmd cmd);
}
