package com.example.domain.validation;

import com.example.ports.GitHubRepositoryPort;
import com.example.ports.SlackNotifierPort;

/**
 * SUT (System Under Test) placeholder.
 * This class represents the logic that needs to be implemented/fixed.
 * Placing it in src/test allows the test to compile against the 'Red' phase requirement
 * if the main source doesn't exist yet, though standard practice is usually to fail compilation
 * until the main source is created.
 * 
 * Here we define the shape of the class we expect the engineer to implement in src/main.
 */
public class DefectReportService {

    private final GitHubRepositoryPort gitHub;
    private final SlackNotifierPort slack;

    public DefectReportService(GitHubRepositoryPort gitHub, SlackNotifierPort slack) {
        this.gitHub = gitHub;
        this.slack = slack;
    }

    /**
     * This method orchestrates the defect reporting workflow.
     * 1. Create Issue in GitHub
     * 2. Post notification to Slack with the URL
     */
    public void execute(ReportDefectCommand cmd) {
        // 1. Create GitHub Issue
        String url = gitHub.createIssue(cmd.title(), cmd.description());

        // 2. Construct Slack Message
        // CURRENT BUG (Suspected): The code likely constructs the message *without* the url variable.
        // EXPECTED BEHAVIOR: The message MUST include the url.
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("*Defect Report Created*\n");
        messageBuilder.append("*Title:*").append(cmd.title()).append("\n");
        // BUG FIX: Append the URL here.
        messageBuilder.append("*GitHub Issue:* <").append(url).append("|View Details>");

        // 3. Send to Slack
        slack.send(cmd.slackChannel(), messageBuilder.toString());
    }
}
