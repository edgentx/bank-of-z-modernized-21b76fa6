package com.example.domain.validation;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Domain Service for handling validation defects.
 * This is the class under test.
 */
@Service
public class ValidationService {

    private final SlackNotificationPort slackNotificationPort;

    public ValidationService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Handles the reporting of a defect (Temporal workflow entry point).
     * Corresponds to the user story: "Trigger _report_defect via temporal-worker exec".
     *
     * @param defectId The ID of the defect (e.g., "VW-454").
     * @param githubUrl The URL to the GitHub issue.
     */
    public void reportDefect(String defectId, String githubUrl) {
        // TDD Green Phase: Include the GitHub URL in the body as required by the defect.
        String slackBody = formulateSlackBody(defectId, githubUrl);
        slackNotificationPort.send("#vforce360-issues", slackBody);
    }

    private String formulateSlackBody(String defectId, String githubUrl) {
        // Green implementation: Ensure the URL is present in the string.
        return "Defect: " + defectId + "\nGitHub Issue: " + githubUrl;
    }
}
