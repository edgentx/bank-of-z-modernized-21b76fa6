package com.example.application;

import com.example.domain.vforce360.model.DefectAggregate;
import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.ports.DefectRepositoryPort;
import com.example.ports.SlackNotifierPort;
import org.springframework.stereotype.Service;

/**
 * Application Service handling the Defect Reporting workflow.
 * Orchestrates the Aggregate, Repository, and Slack Notification.
 * Story S-FB-1: Validates GitHub URL in Slack body.
 */
@Service
public class DefectWorkflowService {

    private final DefectRepositoryPort defectRepository;
    private final SlackNotifierPort slackNotifier;

    public DefectWorkflowService(DefectRepositoryPort defectRepository, SlackNotifierPort slackNotifier) {
        this.defectRepository = defectRepository;
        this.slackNotifier = slackNotifier;
    }

    /**
     * Entry point for the defect reporting workflow.
     * 1. Loads/Creates Aggregate.
     * 2. Executes Command.
     * 3. Persists Aggregate.
     * 4. Publishes Notification with GitHub URL.
     */
    public void reportDefect(ReportDefectCmd cmd) {
        // Load or Create aggregate
        DefectAggregate aggregate = defectRepository.findById(cmd.defectId());
        if (aggregate == null) {
            aggregate = new DefectAggregate(cmd.defectId());
        }

        // Execute logic
        aggregate.execute(cmd);

        // Persist state
        defectRepository.save(aggregate);

        // Publish Event / Notify Slack
        // Construct the body ensuring the URL is present
        String githubUrl = aggregate.getGithubIssueUrl();
        
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("Defect Reported: ").append(aggregate.getTitle()).append("\n");
        bodyBuilder.append("Status: Validation Failed\n");
        bodyBuilder.append("GitHub issue: ").append(githubUrl); // CRITICAL FIX FOR S-FB-1

        slackNotifier.sendMessage("#vforce360-issues", bodyBuilder.toString());
    }
}
