package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import com.example.ports.TemporalWorkflowPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Mock implementation of the Temporal Workflow Port.
 * Simulates the workflow logic that processes the defect report
 * and pushes the notification to the Slack port.
 */
@Component
public class MockTemporalWorkflowAdapter implements TemporalWorkflowPort {

    @Autowired
    private SlackNotificationPort slackNotificationPort;

    @Override
    public void executeReportDefectWorkflow(String issueId) {
        // Simulate the workflow logic that constructs the message body
        // and sends it to Slack.
        
        // Logic Bug (for Red Phase): Previously, this might have just sent a generic message
        // or ignored the URL. We verify it includes the URL now.
        
        String messageBody = "Defect Reported: " + issueId;
        slackNotificationPort.sendMessage(messageBody);
    }
}
