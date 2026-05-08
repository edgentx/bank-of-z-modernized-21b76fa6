package com.example.adapters;

import com.example.domain.shared.DefectReportedEvent;
import com.example.domain.shared.ReportDefectCommand;
import com.example.ports.DefectWorkflowPort;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.WorkflowMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Temporal Workflow implementation for reporting defects.
 * This adapter connects the Spring application to the Temporal durable execution layer.
 */
@Component
@WorkflowImpl(taskQueue = "DEFECT_TASK_QUEUE")
public class TemporalDefectWorkflowAdapter implements DefectWorkflowPort {

    private static final Logger log = LoggerFactory.getLogger(TemporalDefectWorkflowAdapter.class);

    // Injecting the service manually or via WorkflowStub in a real Temporal setup.
    // For the purpose of this Story, we simulate the Workflow execution.
    // In production, Workflows invoke Activities.

    @Autowired
    private com.example.application.DefectReportingService defectReportingService;

    @Override
    @WorkflowMethod
    public String reportDefect(ReportDefectCommand cmd) {
        log.info("Executing workflow for defect: {}", cmd.defectId());

        // 1. Simulate external GitHub Issue creation
        String fakeGithubUrl = createGitHubIssue(cmd);

        // 2. Create Domain Event
        String aggregateId = "agg-" + cmd.defectId();
        DefectReportedEvent event = new DefectReportedEvent(aggregateId, cmd.defectId(), fakeGithubUrl);

        // 3. Trigger downstream notification
        // (In real Temporal, this might be an Activity or a Signal)
        defectReportingService.handleDefectReported(event);

        return fakeGithubUrl;
    }

    private String createGitHubIssue(ReportDefectCommand cmd) {
        // Simulation of GitHub API call
        return "https://github.com/example/issues/" + cmd.defectId().replace("-", "");
    }
}
