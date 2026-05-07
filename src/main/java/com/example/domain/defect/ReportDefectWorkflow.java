package com.example.domain.defect;

import com.example.domain.shared.Command;

/**
 * Workflow interface for reporting a defect.
 */
public interface ReportDefectWorkflow {
    void execute(Command cmd);
}