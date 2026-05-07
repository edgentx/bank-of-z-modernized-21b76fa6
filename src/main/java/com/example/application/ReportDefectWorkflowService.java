package com.example.application;

import com.example.domain.defect.ReportDefectCommand;
import com.example.ports.SlackNotificationPort;
import com.example.ports.VForce360Port;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service implementing the "Report Defect" workflow.
 * Orchestrates the interaction between VForce360 and Slack.
 * This class fixes defect VW-454 by ensuring the GitHub URL is included in the Slack payload.
 */
@Service
public class ReportDefectWorkflowService {

    private final VForce360Port vForce360Port;
    private final SlackNotificationPort slackNotificationPort;

    /**
     * Constructor for dependency injection.
     * @param vForce360Port The port for VForce360 operations.
     * @param slackNotificationPort The port for Slack operations.
     */
    public ReportDefectWorkflowService(VForce360Port vForce360Port, SlackNotificationPort slackNotificationPort) {
        this.vForce360Port = vForce360Port;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Executes the defect reporting workflow.
     * 1. Reports the defect to VForce360.
     * 2. Retrieves the resulting issue URL.
     * 3. Publishes a notification to Slack containing the URL.
     *
     * @param cmd The command containing defect details.
     */
    public void execute(ReportDefectCommand cmd) {
        // Step 1: Report to VForce360
        VForce360Port.DefectRequest request = new VForce360Port.DefectRequest(
            cmd.title(),
            cmd.description(),
            cmd.severity()
        );
        
        String issueUrl = vForce360Port.reportDefect(request);

        // Step 2: Construct Slack Payload with URL (Fix for VW-454)
        String slackMessage = String.format(
            "Defect Reported: %s - %s",
            cmd.title(),
            issueUrl
        );

        Map<String, Object> payload = Map.of(
            "text", slackMessage
        );

        // Step 3: Publish to Slack
        slackNotificationPort.publish("#vforce360-issues", payload);
    }
}
