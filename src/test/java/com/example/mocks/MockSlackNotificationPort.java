package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

@Component
public class MockSlackNotificationPort implements SlackNotificationPort {

    private String lastMessage;

    @Override
    public void sendNotification(String message) {
        this.lastMessage = message;
        System.out.println("[MockSlack] Sent: " + message);
    }

    public String getLastMessage() {
        return lastMessage;
    }
}
