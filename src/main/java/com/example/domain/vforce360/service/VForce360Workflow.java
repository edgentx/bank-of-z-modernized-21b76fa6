package com.example.domain.vforce360.service;

import com.example.domain.vforce360.model.DefectAggregate;
import com.example.ports.SlackNotifier;
import com.example.ports.GitHubClient;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import org.springframework.stereotype.Component;

/**
 * VForce360 Domain Workflow Service.
 * Fixed annotations and added dependencies.
 */
@Component
@WorkflowInterface
public class VForce360Workflow {

    private final SlackNotifier slackNotifier;
    private final GitHubClient gitHubClient;

    // Temporal requires a no-arg constructor for the implementation class, 
    // but we can use a setter or a factory for Spring deps in a real scenario. 
    // For this snippet, we assume a mock-friendly constructor for tests or static access.
    
    public VForce360Workflow(SlackNotifier slackNotifier, GitHubClient gitHubClient) {
        this.slackNotifier = slackNotifier;
        this.gitHubClient = gitHubClient;
    }

    public VForce360Workflow() {
        this.slackNotifier = null;
        this.gitHubClient = null;
    }

    @WorkflowMethod
    public void handleDefectReport(DefectAggregate defect) {
        // 1. Create GitHub Issue
        String url = gitHubClient.createIssue(defect);
        defect.setGithubUrl(url);

        // 2. Notify Slack
        String body = "Defect Reported: " + defect.id() + "\nGitHub Issue: " + url;
        slackNotifier.sendNotification(body);
    }
}
