package com.vforce360.mocks;

import com.vforce360.ports.MarkdownRendererPort;

/**
 * Mock implementation of MarkdownRendererPort.
 * Used in tests to simulate HTML generation.
 */
public class MockMarkdownRendererAdapter implements MarkdownRendererPort {

    @Override
    public String renderToHtml(String markdown) {
        // A simple, predictable mock behavior for testing.
        // In a real mock, we might just verify input, but here we return
        // safe HTML to satisfy the return type.
        if (markdown == null) return "";
        return "<html><body>" + markdown + "</body></html>";
    }
}
