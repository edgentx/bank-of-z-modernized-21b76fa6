package com.example.workflow;

import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.domain.vforce360.model.ReportDefectCmd;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface DefectReportingWorkflow {
    @WorkflowMethod
    DefectReportedEvent reportDefect(ReportDefectCmd cmd);
}
