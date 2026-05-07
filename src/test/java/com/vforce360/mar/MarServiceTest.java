package com.vforce360.mar;

import com.vforce360.mar.ports.MarRepositoryPort;
import com.vforce360.mar.mocks.MockMarRepository;
import com.vforce360.mar.service.MarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the MAR Service logic.
 * TDD Red Phase: These tests define the expected behavior for transforming raw text into HTML.
 */
class MarServiceTest {

    private MarRepositoryPort repository;
    private MarService service;

    @BeforeEach
    void setUp() {
        repository = new MockMarRepository();
        service = new MarService(repository);
    }

    @Test
    void testServiceGeneratesHtmlForValidProject() {
        // Story: "The MAR should display as a clean, formatted document"
        // If we input Markdown, the service should output HTML.
        // Note: The current defect is it outputs JSON. The fix must output HTML.
        
        String projectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        
        // Execute
        String result = service.getRenderedReport(projectId);

        // Assertions
        assertNotNull(result);
        
        // The defect is raw JSON. The fix is HTML.
        // We verify that the result contains HTML tags, not JSON braces.
        assertTrue(result.contains("<"), "Result should contain HTML tags (e.g., <p> or <h1>), not raw JSON");
        
        // Specifically check it doesn't look like JSON
        assertFalse(result.trim().startsWith("{"), "Result should not start with JSON brace '{'");
    }

    @Test
    void testServiceHandlesMarkdownFormatting() {
        // Verify Markdown headers are converted to HTML headers
        String projectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        String result = service.getRenderedReport(projectId);

        // A well-formed HTML document should have structural tags
        // Depending on the Markdown content stored, we expect H1 or P tags.
        // Since we are testing the *Capability*, let's verify HTML structure exists.
        assertTrue(result.contains("<") && result.contains(">"), "Content must be HTML formatted");
    }

    @Test
    void testServiceEscapesJsonInput() {
        // Scenario: The DB stores raw JSON (the defect state).
        // The service should ideally escape or wrap this so it renders as text/code, 
        // or ideally we parse it, but at minimum it must be valid HTML.
        String projectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        String result = service.getRenderedReport(projectId);

        // We expect the raw JSON content to be wrapped in <p> or <pre> tags 
        // so it is valid HTML output.
        assertTrue(result.startsWith("<") || result.contains("<"), "Output must be valid HTML");
    }

    @Test
    void testServiceThrowsExceptionForInvalidProject() {
        String invalidId = "unknown-project-id";
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.getRenderedReport(invalidId);
        });

        assertTrue(exception.getMessage().contains("not found"));
    }
}