package com.example.workflows;

import com.example.vforce.github.model.GithubIssue;
import com.example.vforce.slack.SlackNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Implementation of the Temporal Activity.
 * Bridges Temporal execution with Spring services.
 */
@Component
public class ReportDefectActivityImpl implements ReportDefectActivity {

    private static final Logger log = LoggerFactory.getLogger(ReportDefectActivityImpl.class);
    private final SlackNotificationService slackService;

    // Constructor injection (Implicit in Spring if single constructor, explicit for clarity)
    public ReportDefectActivityImpl(SlackNotificationService slackService) {
        this.slackService = slackService;
    }

    @Override
    public GithubIssue createGithubIssue(String description) {
        // Simulate GitHub API call
        String mockUrl = "https://github.com/egdcrypto/bank-of-z/issues/" + System.currentTimeMillis();
        log.info("[GitHub API] Created issue: {}", mockUrl);
        return new GithubIssue(mockUrl);
    }

    @Override
    public void postSlackNotification(String message, GithubIssue issue) {
        // Delegate to Slack Service
        slackService.postDefectNotification(message, issue);
    }
}
