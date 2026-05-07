package com.vforce360.mocks;

import com.vforce360.ports.MarkdownRendererPort;

/**
 * Mock Adapter for the Markdown Renderer.
 * Simulates the Flexmark/CommonMark library behavior without the heavy dependency or complexity.
 */
public class MockMarkdownRenderer implements MarkdownRendererPort {

    private boolean shouldFail = false;
    private String mockedOutputPrefix = "<div>";
    private String mockedOutputSuffix = "</div>";

    @Override
    public String renderToHtml(String markdown) {
        if (shouldFail) {
            throw new RuntimeException("Mock renderer failure");
        }
        // Simplistic mock logic that mimics a wrapper
        return mockedOutputPrefix + markdown + mockedOutputSuffix;
    }

    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }
}