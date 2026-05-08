package com.example.adapters;

import com.example.ports.SlackPort;
import org.springframework.stereotype.Component;

/**
 * Real implementation for Slack API interactions.
 * Currently a placeholder to satisfy the Port contract.
 */
@Component
public class SlackAdapter implements SlackPort {

    @Override
    public boolean postMessage(String text) {
        // In a real implementation, this would use WebClient
        // to POST to https://slack.com/api/chat.postMessage
        return true;
    }
}
