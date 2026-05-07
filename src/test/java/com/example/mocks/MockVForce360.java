package com.example.mocks;

import com.example.domain.shared.Command;
import com.example.domain.vforce.ports.VForce360Port;

/**
 * Mock implementation of VForce360Port for testing.
 */
public class MockVForce360 implements VForce360Port {

    private String simulatedGitHubUrl;

    public MockVForce360() {
        // Default dummy URL
        this.simulatedGitHubUrl = "https://github.com/example-repo/issues/1";
    }

    public void setSimulatedGitHubUrl(String url) {
        this.simulatedGitHubUrl = url;
    }

    @Override
    public String reportDefect(Command cmd) {
        // Simulate the external call returning a GitHub Issue URL
        return simulatedGitHubUrl;
    }
}