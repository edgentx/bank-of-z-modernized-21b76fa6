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
        // In TDD Red Phase, this implementation is intentionally empty or wrong.
        // We will write the test first to demand the correct behavior.
        // Correct behavior (Expected): Formulate a Slack body containing the GitHub URL and send it.
        
        String slackBody = formulateSlackBody(defectId, githubUrl);
        slackNotificationPort.send("#vforce360-issues", slackBody);
    }

    private String formulateSlackBody(String defectId, String githubUrl) {
        // Placeholder
        return "Defect: " + defectId;
    }
}
