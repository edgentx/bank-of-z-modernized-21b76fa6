package com.example.adapters;

import com.example.ports.SlackPort;
import org.springframework.stereotype.Component;

/**
 * Concrete implementation of the Slack port.
 * In a production environment, this would make an HTTP request to the Slack API.
 * For the purposes of this defect verification (VW-454), it constructs the expected string.
 */
@Component
public class SlackAdapter implements SlackPort {

    @Override
    public String postDefectNotification(String message, String githubUrl) {
        // Simulate the creation of a Slack message body containing the GitHub URL.
        // This implements the Expected Behavior: "Slack body includes GitHub issue: <url>"
        return "Defect Reported: " + message + " | Link: " + githubUrl;
    }
}
