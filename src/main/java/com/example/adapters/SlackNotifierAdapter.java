package com.example.adapters;

import com.example.ports.SlackNotifierPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the SlackNotifierPort.
 * In a production environment, this would make an HTTP call to the Slack API.
 * For this defect fix, we log the output to verify the "Green" phase behavior.
 */
@Component
public class SlackNotifierAdapter implements SlackNotifierPort {

    private static final Logger logger = LoggerFactory.getLogger(SlackNotifierAdapter.class);

    @Override
    public void sendNotification(String messageBody) {
        // Real implementation would use Slack Web API client here
        logger.info("Sending Slack notification: {}", messageBody);
    }
}
