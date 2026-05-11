package com.example.domain.defect.service;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.defect.repository.DefectRepository;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.springframework.stereotype.Service;

@Service
public class DefectWorkflowService {

    private final DefectRepository defectRepository;
    private final GitHubPort gitHubPort;
    private final SlackPort slackPort;

    public DefectWorkflowService(DefectRepository defectRepository, GitHubPort gitHubPort, SlackPort slackPort) {
        this.defectRepository = defectRepository;
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    public void reportDefect(String defectId, String externalRef, String description) {
        // 1. Load or create Aggregate
        DefectAggregate aggregate = defectRepository.findById(defectId)
            .orElse(new DefectAggregate(defectId));

        // 2. Execute Command
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, externalRef, description);
        aggregate.execute(cmd);

        // 3. Persist State
        defectRepository.save(aggregate);

        // 4. Integration Side Effects (GitHub -> Slack)
        // Note: This is a simplified workflow for the defect fix.
        String issueUrl = gitHubPort.createIssue("[" + externalRef + "] " + description, description);

        // 5. Notify Slack
        // Per Acceptance Criteria: Slack body must contain GitHub issue link.
        String slackMessage = String.format(
            "Defect Reported: %s\nDescription: %s\nGitHub Issue: %s",
            externalRef,
            description,
            issueUrl
        );
        
        slackPort.postMessage("#vforce360-issues", slackMessage);
    }
}