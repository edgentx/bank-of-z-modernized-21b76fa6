package com.example.mocks;

import com.example.ports.DefectReporterPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of DefectReporterPort for testing.
 * Captures payloads to verify Slack body contents.
 */
public class MockSlackNotifier implements DefectReporterPort {

    public static class Report {
        public final String defectId;
        public final String githubUrl;

        public Report(String defectId, String githubUrl) {
            this.defectId = defectId;
            this.githubUrl = githubUrl;
        }
    }

    private final List<Report> calls = new ArrayList<>();

    @Override
    public boolean reportDefect(String defectId, String githubUrl) {
        // Simulate recording the call
        calls.add(new Report(defectId, githubUrl));
        return true;
    }

    public List<Report> getCalls() {
        return calls;
    }

    public void reset() {
        calls.clear();
    }
}