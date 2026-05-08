package com.example.adapters;

import com.example.ports.SlackPort;
import org.springframework.stereotype.Component;

/**
 * Real production adapter for Slack.
 * Implements the SlackPort interface.
 * In a full implementation, this would use a Slack WebClient (e.g., using okhttp3 or dedicated SDK)
 * to POST the message to the Slack API.
 */
@Component
public class RealSlackAdapter implements SlackPort {

    @Override
    public void postMessage(String channel, String body) {
        // Production implementation would look like:
        // SlackClient.getInstance().postMessage(channel, body);
        
        // For the scope of this compilation/unit test fix, we leave this as a stub
        // or perform a System.out to simulate the action in a non-mocked environment.
        System.out.println("[Slack] Posting to channel " + channel + ": " + body);
    }
}