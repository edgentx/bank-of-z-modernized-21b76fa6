package com.vforce360.mocks;

import com.vforce360.ports.ReportRendererPort;

/**
 * Mock implementation of ReportRendererPort.
 * We make this "failing" by default or returning specific strings to test business logic flow.
 * For the Red phase, we might instantiate this, but the Service logic will be missing.
 */
public class MockReportRendererPort implements ReportRendererPort {

    private boolean shouldThrowException = false;

    @Override
    public String renderMarkdownToHtml(String markdown) {
        if (shouldThrowException) {
            throw new RuntimeException("Mock renderer failed");
        }
        // Simple mock implementation that just wraps content in <p> tags
        // This ensures that if the Service uses this, it gets a predictable result.
        return "<p>" + markdown + "</p>";
    }

    public void setShouldThrowException(boolean shouldThrowException) {
        this.shouldThrowException = shouldThrowException;
    }
}