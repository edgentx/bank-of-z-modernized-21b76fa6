package com.example.adapters;

import com.example.ports.SlackPort;
import org.springframework.web.client.RestClient;

/**
 * Real adapter for sending Slack messages via Webhook.
 */
public class SlackWebhookAdapter implements SlackPort {

    private final RestClient restClient;
    private final String webhookUrl;

    public SlackWebhookAdapter(RestClient restClient) {
        this(restClient, System.getenv("SLACK_WEBHOOK_URL"));
    }

    public SlackWebhookAdapter(RestClient restClient, String webhookUrl) {
        this.restClient = restClient;
        this.webhookUrl = webhookUrl;
    }

    @Override
    public void sendMessage(String text) {
        // Implementation requires posting to Slack Webhook URL
        // restClient.post()
        //    .uri(webhookUrl)
        //    .body(Map.of("text", text))
        //    .retrieve();
    }
}
