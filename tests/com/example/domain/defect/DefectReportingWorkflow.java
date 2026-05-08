package com.example.domain.defect;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;

/**
 * Implementation stub for the workflow under test.
 * This file represents the 'production' code that needs to be written to pass the test.
 * In TDD Red phase, this might be empty or a stub.
 * For the purpose of this output, I will provide the interface definition required by the test.
 */
public class DefectReportingWorkflow {

    private final SlackNotificationPort slackPort;
    private final GitHubPort gitHubPort;

    public DefectReportingWorkflow(SlackNotificationPort slackPort, GitHubPort gitHubPort) {
        this.slackPort = slackPort;
        this.gitHubPort = gitHubPort;
    }

    public void execute(ReportDefectCmd cmd) {
        // Logic to be implemented:
        // 1. call gitHubPort.createIssue(...)
        // 2. format slack body with URL
        // 3. call slackPort.postMessage(...)
        throw new UnsupportedOperationException("Implement me to pass the test");
    }
}
