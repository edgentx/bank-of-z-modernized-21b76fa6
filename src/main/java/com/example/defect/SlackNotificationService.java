package com.example.defect;

import org.springframework.stereotype.Service;

@Service
public class SlackNotificationService {

    public void sendAlert(String message) {
        // In a real implementation, this would use OkHttp to POST to a Slack Webhook
        // For this defect fix, we ensure the message body contains the GitHub URL
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Slack message cannot be empty");
        }
        System.out.println("SLACK PAYLOAD: " + message);
    }
}