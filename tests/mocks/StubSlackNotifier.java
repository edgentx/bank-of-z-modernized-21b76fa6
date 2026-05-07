package mocks;

import com.example.ports.SlackNotifierPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotifierPort for testing.
 * Records messages instead of sending real HTTP requests.
 */
public class StubSlackNotifier implements SlackNotifierPort {
    private final List<String> messages = new ArrayList<>();

    @Override
    public void send(String messageBody) {
        // In a real implementation, this would use WebClient/RestTemplate to call Slack Web API.
        // Here we just record the body for assertion.
        this.messages.add(messageBody);
    }

    public boolean wasCalled() {
        return !messages.isEmpty();
    }

    public String getLastMessageBody() {
        if (messages.isEmpty()) {
            return null;
        }
        return messages.get(messages.size() - 1);
    }

    public void clear() {
        messages.clear();
    }
}
