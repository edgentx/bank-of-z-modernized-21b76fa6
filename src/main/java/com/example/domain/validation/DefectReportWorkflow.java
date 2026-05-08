package com.example.domain.validation;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.shared.SlackMessageValidator;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface DefectReportWorkflow {
    @WorkflowMethod
    String reportDefect(ReportDefectCmd cmd);
}
