package com.example.mocks;

import com.example.ports.DefectReporterPort;
import com.example.domain.shared.ValidationResult;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for DefectReporterPort.
 * Captures output to memory for verification in tests.
 */
public class MockSlackReporter implements DefectReporterPort {

    public static class Report {
        public final ValidationResult result;
        public final String githubUrl;
        public final String body;

        public Report(ValidationResult result, String githubUrl, String body) {
            this.result = result;
            this.githubUrl = githubUrl;
            this.body = body;
        }
    }

    private final List<Report> reports = new ArrayList<>();

    @Override
    public void reportDefect(ValidationResult result, String githubUrl) {
        // Simulate the formatting that happens in the real implementation
        String body = "Defect reported: " + result.getMessage();
        if (githubUrl != null) {
            body += "\nGitHub issue: " + githubUrl;
        }
        reports.add(new Report(result, githubUrl, body));
    }

    public List<Report> getReports() {
        return reports;
    }

    public void clear() {
        reports.clear();
    }
}
