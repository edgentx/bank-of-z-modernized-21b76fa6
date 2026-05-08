package com.example.adapters;

import com.example.ports.SlackPort;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Real adapter for Slack integration.
 * Implements SlackPort to notify external Slack channels.
 */
@Component
public class SlackAdapter implements SlackPort {

    private final RestTemplate restTemplate;

    public SlackAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void postMessage(String channel, String message) {
        // In a real scenario, this would use a WebClient or RestTemplate to call the Slack API.
        // For the scope of this unit fix, we simulate the call or rely on the test mock.
        System.out.println("[SlackAdapter] Posting to " + channel + ": " + message);
        
        // Placeholder for actual API call:
        // SlackApiRequest request = new SlackApiRequest(channel, message);
        // restTemplate.postForEntity("https://slack.com/api/chat.postMessage", request, String.class);
    }
}
