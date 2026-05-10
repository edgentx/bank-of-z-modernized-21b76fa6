package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Mock implementation of {@link SlackNotificationPort} for testing.
 * Records published payloads to a thread-safe queue for assertions.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final BlockingQueue<String> receivedBodies = new ArrayBlockingQueue<>(100);
    private final BlockingQueue<Map<String, Object>> receivedPayloads = new ArrayBlockingQueue<>(100);

    @Override
    public boolean sendMessage(String channel, String body) {
        // In a real test, we might validate the channel too.
        // For this defect, we focus on the body content.
        this.receivedBodies.offer(body);
        return true;
    }

    @Override
    public boolean sendRichMessage(String channel, Map<String, Object> payload) {
        this.receivedPayloads.offer(payload);
        return true;
    }

    /**
     * Retrieves the last simple string body sent to the mock.
     * Throws AssertionError if nothing was sent.
     */
    public String getLastBody() throws InterruptedException {
        String body = receivedBodies.take();
        if (body == null) {
            throw new AssertionError("No message body received by MockSlack");
        }
        return body;
    }

    /**
     * Retrieves the last rich payload sent to the mock.
     */
    public Map<String, Object> getLastPayload() throws InterruptedException {
        Map<String, Object> payload = receivedPayloads.take();
        if (payload == null) {
            throw new AssertionError("No rich payload received by MockSlack");
        }
        return payload;
    }

    /**
     * Utility to wait and assert the body contains specific text.
     */
    public void assertBodyContains(String text) throws InterruptedException {
        String body = getLastBody();
        if (!body.contains(text)) {
            throw new AssertionError("Expected body to contain [" + text + "] but was: " + body);
        }
    }
}