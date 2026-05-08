package com.example.domain.notification.validation;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Workflow (Orchestration) for reporting a defect.
 * Logic:
 * 1. Accept ReportDefectCommand
 * 2. Generate GitHub URL using GitHubPort
 * 3. Format message body including the URL
 * 4. Send message via SlackPort
 * 
 * This resolves defect VW-454 where the URL was missing.
 */
public class ReportDefectWorkflow {

    private static final Logger logger = LoggerFactory.getLogger(ReportDefectWorkflow.class);
    
    private final SlackPort slackPort;
    private final GitHubPort gitHubPort;

    // Spring will inject the concrete adapters implementing these ports
    public ReportDefectWorkflow(SlackPort slackPort, GitHubPort gitHubPort) {
        this.slackPort = slackPort;
        this.gitHubPort = gitHubPort;
    }

    /**
     * Executes the defect reporting logic.
     * @param cmd Command containing issue details.
     */
    public void execute(ReportDefectCommand cmd) throws ExecutionException, InterruptedException {
        logger.info("Executing ReportDefect for issue: {}", cmd.issueId());

        // 1. Generate the GitHub URL
        String issueUrl = gitHubPort.generateIssueUrl(cmd.issueId());

        // 2. Construct the message body
        // Defect VW-454 fix: Ensure the URL is actually in the body.
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("Defect Reported: ").append(cmd.description()).append("\n");
        bodyBuilder.append("Issue ID: ").append(cmd.issueId()).append("\n");
        bodyBuilder.append("GitHub Issue: ").append(issueUrl).append("\n"); // The fix

        String messageBody = bodyBuilder.toString();

        // 3. Send to Slack (#vforce360-issues as per story)
        String channel = "#vforce360-issues";
        
        logger.debug("Sending message to Slack channel {}: {}", channel, messageBody);
        
        // Block for completion to satisfy workflow contract (or return Future if async workflow)
        slackPort.sendMessage(channel, messageBody).get();
    }
}
