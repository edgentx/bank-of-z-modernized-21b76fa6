package com.example.ports;

import java.util.Map;

public interface SlackNotifier {
    void sendNotification(String webhookUrl, String text, Map<String, Object> attachments);
}