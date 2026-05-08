package com.example.workflow;

import com.example.domain.validation.model.ReportDefectCommand;
import com.example.ports.SlackNotificationPort;

/**
 * Implementation of DefectActivities.
 * This class performs the actual logic to construct the message and invoke the Slack port.
 */
public class DefectActivitiesImpl implements DefectActivities {

    private final SlackNotificationPort slackNotificationPort;

    // Constructor injection used by Temporal Worker factory
    public DefectActivitiesImpl(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    @Override
    public void notifySlack(ReportDefectCommand command) {
        String defectId = command.defectId();
        // Format strictly follows the test expectation: "Defect reported: ID. GitHub issue: URL"
        String body = String.format(
            "Defect reported: %s. GitHub issue: https://github.com/example/issues/%s",
            defectId,
            defectId
        );
        
        slackNotificationPort.send("#vforce360-issues", body);
    }
}
