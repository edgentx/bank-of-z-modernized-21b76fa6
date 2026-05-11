package com.example.mocks;

import com.example.ports.SlackNotifier;

/**
 * Spy implementation of SlackNotifier for testing.
 * Captures the last body sent to allow verification in tests.
 */
public class SpySlackNotifier implements SlackNotifier {
    private String lastBody;

    @Override
    public void notify(String body) {
        this.lastBody = body;
    }

    public String getLastBody() {
        return lastBody;
    }
}
