package com.example.workflows;

import com.example.vforce.adapter.SlackNotificationAdapter;
import com.example.workflow.DefectReportActivities;
import io.temporal.activity.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Implementation of Temporal Activities for defect reporting.
 * Wraps external interactions (GitHub API, Slack Adapter) for the workflow.
 */
@Component
public class DefectReportActivitiesImpl implements DefectReportActivities {

    private static final Logger log = LoggerFactory.getLogger(DefectReportActivitiesImpl.class);

    private final SlackNotificationAdapter slackAdapter;

    @Autowired
    public DefectReportActivitiesImpl(SlackNotificationAdapter slackAdapter) {
        this.slackAdapter = slackAdapter;
    }

    @Override
    public String createGitHubIssue(String title, String description, String severity) {
        // Simulate GitHub API call latency/logic
        Activity.heartbeat(null);
        
        // Stub implementation returning a deterministic URL format based on inputs
        // In a real scenario, this would use a GitHubClient (HTTP) adapter.
        String mockIssueId = "issue-" + System.currentTimeMillis();
        log.info("Created GitHub issue {} for title: {}", mockIssueId, title);
        
        return "https://github.com/fake/issues/" + mockIssueId;
    }

    @Override
    public void sendSlackNotification(String messageBody) {
        log.info("Sending Slack notification with body length: {}", messageBody.length());
        // Delegate to the VForce Slack Adapter
        // The Adapter is responsible for formatting the message for the specific channel
        slackAdapter.sendMessage(messageBody);
    }
}
