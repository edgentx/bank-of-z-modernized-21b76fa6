package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Spy implementation of SlackNotificationPort for testing.
 * Captures arguments passed to postDefect for assertions.
 */
public class SpySlackNotificationPort implements SlackNotificationPort {

    private final List<Call> calls = new ArrayList<>();

    @Override
    public void postDefect(String defectTitle, String body) {
        calls.add(new Call(defectTitle, body));
    }

    public boolean wasCalled() {
        return !calls.isEmpty();
    }

    public String getLastBody() {
        if (calls.isEmpty()) return null;
        return calls.get(calls.size() - 1).body;
    }

    public String getLastTitle() {
        if (calls.isEmpty()) return null;
        return calls.get(calls.size() - 1).title;
    }
    
    public int getCallCount() {
        return calls.size();
    }

    private record Call(String title, String body) {}
}
