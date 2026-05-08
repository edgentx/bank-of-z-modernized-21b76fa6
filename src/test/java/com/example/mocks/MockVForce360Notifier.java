package com.example.mocks;

import com.example.domain.vforce360.ports.VForce360NotifierPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of the Notifier Port for testing.
 */
public class MockVForce360Notifier implements VForce360NotifierPort {
    public final List<String> sentBodies = new ArrayList<>();

    @Override
    public void sendDefectReport(String body) {
        sentBodies.add(body);
    }

    public String getLastSentBody() {
        if (sentBodies.isEmpty()) return null;
        return sentBodies.get(sentBodies.size() - 1);
    }
}
