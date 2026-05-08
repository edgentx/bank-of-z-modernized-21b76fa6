package com.example.validation;

import com.example.ports.SlackPort;
import com.example.validation.model.DefectReportCommand;

/**
 * Validator service responsible for processing defect reports and
 * ensuring they are formatted correctly for Slack notifications.
 * 
 * Story: S-FB-1 (Fix: Validating VW-454)
 * Role: Constructs the Slack body with the required GitHub URL.
 */
public class SlackNotificationValidator {

    private final SlackPort slackPort;

    public SlackNotificationValidator(SlackPort slackPort) {
        this.slackPort = slackPort;
    }

    /**
     * Processes the defect report command and triggers a Slack notification.
     * 
     * Expected behavior:
     * 1. Generate GitHub issue URL based on the defect ID.
     * 2. Format the Slack body text.
     * 3. Invoke the Slack port.
     *
     * @param command The defect report command containing ID and project context.
     */
    public void processDefectReport(DefectReportCommand command) {
        // Implementation required to pass the test.
        // Current stub will fail the TDD Red phase test.
        throw new UnsupportedOperationException("Implement Slack generation logic");
    }
}
