package com.example.domain.validation;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;

/**
 * Placeholder service class for the TDD Red Phase.
 * This class represents the implementation required to pass the tests.
 * In the actual 'Red' phase, this stub would likely be missing or empty,
 * causing the tests to fail. Here we define it so the code compiles,
 * but the logic inside is intentionally insufficient/incorrect to demonstrate
 * failure if specific logic were implemented, OR simply empty to throw exceptions.
 * 
 * For the sake of this artifact, we provide a skeleton.
 */
public class DefectReportingService {

    private final SlackNotificationPort slackPort;
    private final GitHubIssuePort gitHubPort;

    public DefectReportingService(SlackNotificationPort slackPort, GitHubIssuePort gitHubPort) {
        this.slackPort = slackPort;
        this.gitHubPort = gitHubPort;
    }

    /**
     * Reports a defect to Slack.
     * This method is currently a STUB for the Red Phase.
     * It does NOT implement the logic to append the URL yet.
     */
    public void reportDefect(String defectId, String channel) {
        if (defectId == null) {
            throw new IllegalArgumentException("defectId cannot be null");
        }
        
        // INTENTIONAL BUG FOR RED PHASE:
        // We construct the body WITHOUT the URL, failing the validation test.
        String body = "Defect reported: " + defectId; 
        
        // If we implemented the fix, it would look like:
        // String url = gitHubPort.getIssueUrl(defectId);
        // String body = "Defect reported: " + defectId + "\n" + url;

        slackPort.postMessage(channel, body);
    }
}
