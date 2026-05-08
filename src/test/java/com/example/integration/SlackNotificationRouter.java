package com.example.integration;

import com.example.domain.shared.Command;
import com.example.domain.shared.UnknownCommandException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Mock Slack Router for testing.
 * Implements a port interface that would normally exist in src/ports.
 * For the purposes of this defect fix, we create it in the test scope to simulate the external system.
 */
public class SlackNotificationRouter {

    private String lastMessageBody;
    private boolean sent = false;

    public void send(String messageBody) {
        this.lastMessageBody = messageBody;
        this.sent = true;
        System.out.println("[SLACK MOCK] Sent: " + messageBody);
    }

    public String getLastMessageBody() {
        return lastMessageBody;
    }

    public boolean wasSent() {
        return sent;
    }

    public void reset() {
        this.lastMessageBody = null;
        this.sent = false;
    }
}
