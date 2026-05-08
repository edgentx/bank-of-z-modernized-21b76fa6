package com.example.application;

import io.temporal.spring.boot.ActivityImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@ActivityImpl(taskQueue = "DefectReportingTaskQueue")
public class DefectReportingActivities implements DefectReportingActivityInterface {

    private final SlackNotificationService slackService;

    public DefectReportingActivities(SlackNotificationService slackService) {
        this.slackService = slackService;
    }

    @Override
    public String reportDefect(String defectDetails) {
        // Mock logic to generate a GitHub URL
        String githubIssueId = "GH-" + UUID.randomUUID().toString().substring(0, 8);
        String githubUrl = "https://github.com/bank-of-z/issues/" + githubIssueId;

        // Format the message body
        String messageBody = String.format(
            "Defect Reported: %s\nGitHub Issue: %s",
            defectDetails, githubUrl
        );

        slackService.sendNotification("#vforce360-issues", messageBody);

        return githubUrl;
    }
}
