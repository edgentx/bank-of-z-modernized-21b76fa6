package com.example.adapters;

import com.example.ports.SlackPort;
import org.springframework.stereotype.Component;

/**
 * Slack implementation for posting messages.
 * In a real scenario, this would use the Slack WebClient API.
 * For the scope of this defect fix, we stub the network interaction or log.
 */
@Component
public class SlackAdapter implements SlackPort {

    @Override
    public void postMessage(String body) {
        // Real implementation would use Slack WebApi to post to a channel.
        // For the purpose of fixing the validation logic defect:
        // We assume the transport works; the validation is on the content.
        if (body == null || body.isBlank()) {
            throw new IllegalArgumentException("Slack message body cannot be null or blank");
        }
        
        System.out.println("[SlackAdapter] Posting message: " + body);
        // webClient.chatPostMessage(r -> r.channel("#vforce360-issues").text(body));
    }
}
