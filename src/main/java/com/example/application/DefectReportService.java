package com.example.application;

import com.example.domain.shared.ReportDefectCmd;
import com.example.workflows.ReportDefectWorkflow;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Application Service that orchestrates the defect reporting via Temporal.
 */
@Service
public class DefectReportService {

    private static final Logger log = LoggerFactory.getLogger(DefectReportService.class);
    
    private final ReportDefectWorkflow defectWorkflow;
    private final GitHubPort gitHubPort;
    private final SlackPort slackPort;

    // In a real Spring + Temporal setup, the Workflow stub is injected via WorkerFactory.
    // For testing/compilation purposes, we show constructor injection.
    public DefectReportService(ReportDefectWorkflow defectWorkflow, GitHubPort gitHubPort, SlackPort slackPort) {
        this.defectWorkflow = defectWorkflow;
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    public String reportDefect(ReportDefectCmd cmd) {
        log.info("Processing defect report command: {}", cmd.defectId());
        
        // Workflow execution is async in production, but we return the resulting ID/URL for logic flow
        return defectWorkflow.execute(cmd.summary(), cmd.description(), "#vforce360-issues");
    }
}
