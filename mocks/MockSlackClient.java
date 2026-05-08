package mocks;

/**
 * Mock adapter for Slack API interactions.
 * Used in tests to avoid real network calls.
 */
public class MockSlackClient {
    public String lastPostedMessage;
    
    public void postMessage(String channel, String text) {
        this.lastPostedMessage = text;
        System.out.println("[Mock Slack] Posted to " + channel + ": " + text);
    }
}