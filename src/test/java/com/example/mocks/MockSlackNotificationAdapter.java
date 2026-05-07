package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

public class MockSlackNotificationAdapter implements SlackNotificationPort {
    private final List<CallLog> calls = new ArrayList<>();

    public static class CallLog {
        public final String message;
        public final String details;

        public CallLog(String message, String details) {
            this.message = message;
            this.details = details;
        }
    }

    @Override
    public void postMessage(String message, String details) {
        calls.add(new CallLog(message, details));
    }

    public List<CallLog> getCalls() {
        return calls;
    }
    
    public CallLog getFirstCall() {
        if (calls.isEmpty()) return null;
        return calls.get(0);
    }

    public void reset() {
        calls.clear();
    }
}