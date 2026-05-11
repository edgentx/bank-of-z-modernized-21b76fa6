package com.example.mocks;

import com.example.ports.SlackMessageValidator;
import com.example.domain.shared.ReportDefectCmd;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackMessageValidator for testing.
 * Captures messages to verify content without calling the network.
 */
@Component
public class MockSlackMessageValidator implements SlackMessageValidator {

    public final List<String> sentMessages = new ArrayList<>();
    public String lastFormattedMessage;

    @Override
    public String validateAndFormat(ReportDefectCmd cmd) {
        // Simplified formatting for the mock, acting as the "Real" implementation logic under test
        // or simply passing through if we are testing the formatter.
        // For this test, we assume the implementation constructs the string.
        // We will allow the system under test to generate the content, but here we just verify
        // the contract.
        return "Title: " + cmd.title();
    }

    @Override
    public boolean send(String formattedMessage) {
        this.lastFormattedMessage = formattedMessage;
        this.sentMessages.add(formattedMessage);
        return true;
    }

    public void reset() {
        sentMessages.clear();
        lastFormattedMessage = null;
    }
}
