package com.example.adapters;

import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

/**
 * Adapter implementation that delegates to the MockSlackNotificationPort.
 * This bridges the gap between the Port interface and the Mock class used in tests,
 * allowing Spring to inject a valid implementation that the tests can inspect.
 */
@Component
public class SlackNotificationStubAdapter implements SlackNotificationPort {

    // We hold a static reference to the mock instance used by the tests.
    // This allows the Spring-injected adapter to write to the same list the tests verify.
    // Note: In a real environment, this would be an HTTP client to Slack Web API.
    private static MockSlackNotificationPort testDelegate = new MockSlackNotificationPort();

    /**
     * Package-private method to inject the specific mock instance from the test context.
     * This is often called in @PostConstruct or a test setup utility, but for simplicity,
     * we allow static injection if needed, or default to a new instance.
     */
    public static void setTestDelegate(MockSlackNotificationPort mock) {
        testDelegate = mock;
    }

    @Override
    public boolean sendMessage(String channel, String messageBody) {
        // Delegate the call to the mock instance so tests can verify the message content.
        return testDelegate.sendMessage(channel, messageBody);
    }
}