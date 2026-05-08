package com.example.mocks;

import com.example.ports.TemporalWorkerPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for TemporalWorkerPort.
 * Records workflow invocations to verify behavior during tests.
 */
public class MockTemporalWorker implements TemporalWorkerPort {

    private final List<String> reportedDefects = new ArrayList<>();

    @Override
    public void reportDefect(String defectId) {
        System.out.println("[MockTemporalWorker] Received reportDefect command for ID: " + defectId);
        reportedDefects.add(defectId);
    }

    public boolean hasReported(String defectId) {
        return reportedDefects.contains(defectId);
    }

    public void reset() {
        reportedDefects.clear();
    }
}