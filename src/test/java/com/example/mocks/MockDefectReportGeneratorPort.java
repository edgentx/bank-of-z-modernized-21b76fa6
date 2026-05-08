package com.example.mocks;

import com.example.ports.DefectReportGeneratorPort;
import com.example.domain.shared.Command;

/**
 * Mock implementation of DefectReportGeneratorPort.
 * Returns a predictable GitHub URL for testing.
 */
public class MockDefectReportGeneratorPort implements DefectReportGeneratorPort {

    private static final String MOCK_GITHUB_URL = "https://github.com/bank-of-z/vforce360/issues/454";

    @Override
    public String generateDefectReportUrl(Command cmd) {
        // In a real scenario, this might interact with GitHub API.
        // For testing, we always return a valid URL structure.
        return MOCK_GITHUB_URL;
    }
}