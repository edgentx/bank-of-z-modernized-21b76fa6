package com.example.mocks;

import com.example.ports.VForce360Port;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of VForce360Port for testing.
 * Captures reported bodies to verify content without external I/O.
 */
public class MockVForce360Port implements VForce360Port {

    private final List<Report> capturedReports = new ArrayList<>();

    @Override
    public boolean reportDefect(String defectId, String body) {
        // Store the interaction for verification
        capturedReports.add(new Report(defectId, body));
        return true;
    }

    /**
     * Returns the list of all reports made to this mock.
     */
    public List<Report> getCapturedReports() {
        return capturedReports;
    }

    /**
     * Clears the captured history.
     */
    public void clear() {
        capturedReports.clear();
    }

    /**
     * Value object representing a single report request.
     */
    public record Report(String defectId, String body) {}
}
