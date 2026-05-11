package com.example.application;

import com.example.ports.GitHubPort;
import com.example.ports.ReportDefectPort;
import com.example.slack.model.ReportDefectCmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service implementation of ReportDefectPort.
 * Handles the logic of reporting a defect and generating the Slack body.
 */
@Service
public class ReportDefectService implements ReportDefectPort {

    private static final Logger log = LoggerFactory.getLogger(ReportDefectService.class);
    private final GitHubPort gitHubPort;

    public ReportDefectService(GitHubPort gitHubPort) {
        this.gitHubPort = gitHubPort;
    }

    @Override
    public String executeReportDefectWorkflow(ReportDefectCmd cmd) {
        log.info("Executing defect report for: {}", cmd.defectId());

        // 1. Generate the GitHub Issue URL using the Port
        String url = gitHubPort.createIssueUrl(cmd.description()).toString();

        // 2. Format the Slack body
        // Expected Behavior: Slack body includes GitHub issue: <url>
        return String.format("Defect Reported: %s%nGitHub issue: %s", cmd.defectId(), url);
    }
}
