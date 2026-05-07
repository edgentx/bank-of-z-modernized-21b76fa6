package com.example.adapters;

import com.example.domain.validation.DefectReportedEvent;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the Slack notification port.
 * In a production environment, this would use a Slack WebClient (e.g., Slack SDK) to POST the message.
 * For the purpose of this defect validation, we log the transmission to verify the body content.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void notifyDefectReported(DefectReportedEvent event) {
        // Here we would use SlackApiClient.postMessage(event.slackBody())
        // For validation VW-454, we ensure the body is passed correctly.
        log.info("Sending Slack notification for defect {}: {}", event.defectId(), event.slackBody());
        
        // Simulate successful transmission
        log.debug("GitHub URL in payload: {}", event.githubUrl());
    }
}
