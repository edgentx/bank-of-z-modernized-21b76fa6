package com.example.workflows;

import com.example.ports.SlackPort;
import com.example.domain.validation.model.SlackNotificationMessage;
import io.temporal.activity.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Activity implementation connecting to external services (GitHub, Slack).
 */
@Component
public class ReportDefectActivityImpl implements ReportDefectActivity {

    private final SlackPort slackPort;

    @Autowired
    public ReportDefectActivityImpl(SlackPort slackPort) {
        this.slackPort = slackPort;
    }

    @Override
    public String createGitHubIssue(String description, String severity) {
        // Simulate API call
        Activity.sleep(100); // Simulate network latency
        return "https://github.com/example-bank/z/issues/1";
    }

    @Override
    public void notifySlack(String text) {
        slackPort.sendNotification(SlackNotificationMessage.of("#vforce360-issues", text));
    }
}