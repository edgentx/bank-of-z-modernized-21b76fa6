package com.example.mocks;

import com.example.ports.VForce360DiagnosticsPort;

/**
 * Mock implementation of VForce360DiagnosticsPort for testing.
 * Returns predictable data without hitting the external VForce360 API.
 */
public class MockVForce360DiagnosticsAdapter implements VForce360DiagnosticsPort {

    private String configuredUrl;

    @Override
    public String getDiagnosticContext(String defectId) {
        return "Diagnostic data for " + defectId;
    }

    @Override
    public String resolveGitHubUrl(String defectId) {
        // Return a pre-configured URL string, or a default if not set.
        // This allows the test to inject the specific URL it wants to verify.
        if (configuredUrl != null) {
            return configuredUrl;
        }
        return "https://github.com/example-org/bank-of-z/issues/default";
    }

    /**
     * Test helper to set what URL should be returned.
     */
    public void setResolvedGitHubUrl(String url) {
        this.configuredUrl = url;
    }
}
