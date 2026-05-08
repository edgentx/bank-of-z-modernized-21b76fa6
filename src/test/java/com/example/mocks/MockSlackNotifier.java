package com.example.mocks;

import com.example.ports.SlackNotifier;

import java.util.Map;

public class MockSlackNotifier implements SlackNotifier {
    public String lastWebhookUrl;
    public String lastText;
    public Map<String, Object> lastAttachments;
    public boolean notificationSent = false;

    @Override
    public void sendNotification(String webhookUrl, String text, Map<String, Object> attachments) {
        this.lastWebhookUrl = webhookUrl;
        this.lastText = text;
        this.lastAttachments = attachments;
        this.notificationSent = true;
    }

    public void reset() {
        this.lastWebhookUrl = null;
        this.lastText = null;
        this.lastAttachments = null;
        this.notificationSent = false;
    }
}