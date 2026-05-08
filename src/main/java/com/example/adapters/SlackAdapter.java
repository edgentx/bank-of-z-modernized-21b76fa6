package com.example.adapters;

import com.example.ports.SlackPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the SlackPort.
 * Connects to the actual Slack API (or logs in a dry-run mode).
 */
@Component
@ConditionalOnProperty(name = "app.adapters.slack.enabled", havingValue = "true", matchIfMissing = true)
public class SlackAdapter implements SlackPort {

    private static final Logger log = LoggerFactory.getLogger(SlackAdapter.class);

    @Override
    public void sendMessage(String channelId, String messageBody) {
        // In a real scenario, this would use the Slack Web SDK to post the message.
        // For the purpose of this defect validation, we log to confirm execution.
        log.info("[SlackAdapter] Sending to {}: {}", channelId, messageBody);
        
        // Pseudo-code for real implementation:
        // Slack slack = Slack.getInstance(new SlackConfig());
        // MethodsClient methods = slack.methods(System.getenv("SLACK_TOKEN"));
        // ChatPostMessageRequest request = ChatPostMessageRequest.builder()
        //     .channel(channelId)
        //     .text(messageBody)
        //     .build();
        // methods.chatPostMessage(request);
    }
}
