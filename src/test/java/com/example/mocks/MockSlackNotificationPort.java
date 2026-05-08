package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures sent messages for verification without real I/O.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<SendResult> sentMessages = new ArrayList<>();
    private boolean shouldFail = false;

    @Override
    public SendResult reportDefect(String defectId, String description) {
        if (shouldFail) {
            return new SendResult(false, "Simulated Slack API Failure");
        }
        
        // In a real scenario, the body is constructed by the system under test.
        // However, for the mock adapter pattern used in testing existing flows,
        // we can simulate the receipt of a message.
        // Here, we simply record that the method was called.
        // Note: For THIS specific test (VW-454), we are testing the BODY generation.
        // So this mock is used if the system calls Slack. If we are testing a Formatter,
        // we might not need this, but assuming the application flow uses this port.
        
        String body = "Defect reported: " + defectId;
        SendResult result = new SendResult(true, body);
        sentMessages.add(result);
        return result;
    }

    public List<SendResult> getSentMessages() {
        return sentMessages;
    }

    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }
    
    public void clear() {
        sentMessages.clear();
    }
}
