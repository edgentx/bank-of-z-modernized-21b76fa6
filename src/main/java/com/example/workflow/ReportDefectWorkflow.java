package com.example.workflow;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import io.temporal.spring.boot.WorkflowImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Temporal Workflow implementation for reporting defects.
 * Orchestrates the creation of a GitHub issue and notification via Slack.
 */
@WorkflowImpl(taskQueues = "REPORT_DEFECT_TASK_QUEUE")
public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    private static final Logger log = LoggerFactory.getLogger(ReportDefectWorkflowImpl.class);

    private final GitHubPort gitHubPort;
    private final SlackPort slackPort;
    private final String slackChannel;

    public ReportDefectWorkflowImpl(GitHubPort gitHubPort, SlackPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
        // In a real Spring Boot app, this would be @Value("${slack.channel.defects}")
        this.slackChannel = "#vforce360-issues";
    }

    @Override
    public String report(String title, String body) {
        log.info("Executing defect report for: {}", title);

        // 1. Create Issue in GitHub
        String issueUrl = gitHubPort.createIssue(title, body);
        log.info("GitHub issue created: {}", issueUrl);

        // 2. Notify Slack
        // CRITICAL FIX for VW-454: Ensure the URL is present in the message text.
        String messageText = "Defect reported. GitHub issue: " + issueUrl;
        
        slackPort.sendMessage(slackChannel, messageText, List.of());
        
        log.info("Slack notification sent to {}", slackChannel);
        
        return issueUrl;
    }
}