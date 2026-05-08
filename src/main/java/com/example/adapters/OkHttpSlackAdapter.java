package com.example.adapters;

import com.example.ports.SlackWebhookPort;
import org.springframework.stereotype.Component;

/**
 * Production implementation of SlackWebhookPort using OkHttp.
 */
@Component
public class OkHttpSlackAdapter implements SlackWebhookPort {

    @Override
    public void send(String jsonPayload) {
        // Implementation Note:
        // Real code would use OkHttpClient to POST the jsonPayload to the Slack Webhook URL.
        throw new UnsupportedOperationException("Production Slack API call not implemented in this context");
    }
}
