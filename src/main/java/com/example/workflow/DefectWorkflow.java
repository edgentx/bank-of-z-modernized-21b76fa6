package com.example.workflow;

import com.example.domain.defect.model.ReportDefectCmd;

/**
 * Workflow Interface for reporting defects.
 */
public interface DefectWorkflow {
    void reportDefect(ReportDefectCmd cmd);
}