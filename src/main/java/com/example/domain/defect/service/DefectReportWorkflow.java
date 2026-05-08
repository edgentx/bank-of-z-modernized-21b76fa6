package com.example.domain.defect.service;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.temporal.activity.ActivityInterface;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.util.concurrent.CompletableFuture;

@WorkflowInterface
public interface DefectReportWorkflow {

    @WorkflowMethod
    void reportDefect(ReportDefectCmd cmd);

}
