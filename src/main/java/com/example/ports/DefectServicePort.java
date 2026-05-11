package com.example.ports;

import com.example.domain.defect.model.ReportDefectCommand;

/**
 * Port interface for Defect Reporting Service.
 * This abstraction allows us to mock the service behavior in tests
 * without connecting to the real Temporal workflow engine.
 */
public interface DefectServicePort {
    void reportDefect(ReportDefectCommand cmd);
}
