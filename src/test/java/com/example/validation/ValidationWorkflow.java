package com.example.validation;

import com.example.domain.shared.ReportDefectCmd;

/**
 * Temporal Workflow interface for defect reporting.
 */
public interface ValidationWorkflow {
    void reportDefect(ReportDefectCmd cmd);
}
