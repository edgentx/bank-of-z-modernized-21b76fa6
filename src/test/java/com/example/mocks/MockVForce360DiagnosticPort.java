package com.example.mocks;

import com.example.ports.VForce360DiagnosticPort;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock adapter for VForce360 diagnostics.
 * Allows tests to control what GitHub URL is returned for a specific defect ID.
 */
public class MockVForce360DiagnosticPort implements VForce360DiagnosticPort {

    private final Map<String, String> database = new HashMap<>();
    private String defaultUrl;

    public void setNextIssueUrl(String url) {
        this.defaultUrl = url;
    }

    public void registerDefect(String defectId, String url) {
        database.put(defectId, url);
    }

    @Override
    public String fetchDefectLink(String defectId) {
        // Simulate a lookup or external API call
        if (database.containsKey(defectId)) {
            return database.get(defectId);
        }
        return defaultUrl;
    }
}