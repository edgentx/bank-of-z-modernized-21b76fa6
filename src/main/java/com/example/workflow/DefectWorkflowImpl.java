package com.example.workflow;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;

/**
 * Implementation of the Defect Workflow.
 * Currently a stub to allow compilation of tests.
 */
public class DefectWorkflowImpl implements DefectWorkflow {

    private final SlackPort slackPort;
    private final GitHubPort gitHubPort;

    public DefectWorkflowImpl(SlackPort slackPort, GitHubPort gitHubPort) {
        this.slackPort = slackPort;
        this.gitHubPort = gitHubPort;
    }

    @Override
    public void reportDefect(ReportDefectCmd cmd) {
        // TODO: Implement orchestration logic
    }
}