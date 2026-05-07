package com.example.domain.validation;

import com.example.ports.SlackNotificationPort;

/**
 * Service handling defect reporting logic.
 * This class is expected to be implemented to make the test pass.
 * Placed in test structure temporarily or as a placeholder for the main implementation.
 */
public class ValidationService {

    private final SlackNotificationPort slackNotificationPort;

    public ValidationService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    public void reportDefect(DefectReportedCommand cmd) {
        // Implementation pending.
        // Logic should format a message containing cmd.githubUrl and send it to the port.
        throw new UnsupportedOperationException("Implement S-FB-1 defect reporting logic here");
    }
}
