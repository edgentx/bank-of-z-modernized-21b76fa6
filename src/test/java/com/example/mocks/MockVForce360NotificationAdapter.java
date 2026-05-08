package com.example.mocks;

import com.example.ports.VForce360NotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for VForce360 notifications.
 * Implements the Port interface to satisfy the dependency injection requirements,
 * allowing unit tests to run without connecting to the real VForce360 API.
 */
public class MockVForce360NotificationAdapter implements VForce360NotificationPort {

    private final List<String> reportedDefects = new ArrayList<>();

    @Override
    public String reportDefect(String defectId, String summary) {
        // Simulate external side-effect: record the attempt
        this.reportedDefects.add(defectId);
        // Return a fake correlation ID
        return "mock-vforce-" + defectId;
    }

    public boolean wasReported(String defectId) {
        return reportedDefects.contains(defectId);
    }
}
