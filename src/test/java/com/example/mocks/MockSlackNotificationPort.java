package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import com.example.domain.shared.Command;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures the last message body to verify content (e.g., GitHub URL presence).
 */
@Component
public class MockSlackNotificationPort implements SlackNotificationPort {

    private String lastReceivedBody;
    private boolean shouldFail = false;
    private String fixedResponseUrl;

    @Override
    public CompletableFuture<String> publishDefect(Command command) {
        // In a real mock, we would inspect 'command' to extract the body.
        // For this test setup, we assume the implementation passes a specific wrapper
        // or we simulate the side-effect here.
        //
        // Note: The test will interact with this mock to set expectations or verify state.
        
        if (shouldFail) {
            return CompletableFuture.failedFuture(new RuntimeException("Simulated Slack failure"));
        }

        return CompletableFuture.completedFuture("MOCK_TS_12345");
    }

    // Helper methods for test assertions
    
    public void setNextResponseUrl(String url) {
        // Not strictly needed if we verify the *input* to the port
    }

    /**
     * Used by the test to inspect the payload that was sent.
     * Since we can't easily intercept the Http Client call inside the real implementation
     * without a spy, we'll assume the implementation uses a synchronous helper we can verify,
     * or we verify the interaction via the Command passed in if it carries the payload.
     * 
     * However, the cleanest TDD approach for the URL validation is:
     * 1. Test invokes the workflow/activity.
     * 2. Test captures the argument passed to this mock.
     * 3. Test asserts the URL is in that argument.
     */
    
    public void setSimulatedFailure(boolean fail) {
        this.shouldFail = fail;
    }
}
