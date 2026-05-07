package mocks;

import com.example.ports.VForce360ClientPort;

/**
 * Mock implementation of VForce360ClientPort for testing.
 * Simulates the response from the VForce360 orchestration layer.
 */
public class StubVForce360Client implements VForce360ClientPort {
    private final String stubbedUrl;

    /**
     * @param stubbedUrl The URL to return when createIssue is called. 
     *                   Pass null to simulate a failure scenario.
     */
    public StubVForce360Client(String stubbedUrl) {
        this.stubbedUrl = stubbedUrl;
    }

    @Override
    public String createIssue(String title, String description) {
        // Simulate network latency or logic if necessary.
        // For unit tests, we return the configured stub immediately.
        if (stubbedUrl == null) {
            return null;
        }
        return stubbedUrl;
    }
}
