package com.example.domain.vforce360.service;

import com.example.domain.defect.model.ReportDefectCmd;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface VForce360Workflow {
    @WorkflowMethod
    void reportDefect(ReportDefectCmd cmd);
}
