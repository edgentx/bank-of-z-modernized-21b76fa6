package com.example.mocks;

import com.example.ports.VForce360NotificationPort;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mock adapter for VForce360 notifications.
 * Stores sent payloads in memory for verification in tests.
 */
public class MockVForce360NotificationPort implements VForce360NotificationPort {

    private final Map<String, Map<String, String>> sentReports = new ConcurrentHashMap<>();
    private boolean shouldFail = false;

    @Override
    public boolean reportDefect(String defectId, Map<String, String> metadata) {
        if (shouldFail) return false;
        sentReports.put(defectId, new HashMap<>(metadata));
        return true;
    }

    public Map<String, String> getReport(String defectId) {
        return sentReports.get(defectId);
    }

    public Set<String> getReportedDefectIds() {
        return sentReports.keySet();
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }

    public void reset() {
        sentReports.clear();
        this.shouldFail = false;
    }
}
