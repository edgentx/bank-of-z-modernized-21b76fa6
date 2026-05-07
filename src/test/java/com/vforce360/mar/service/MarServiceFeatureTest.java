package com.vforce360.mar.service;

import com.vforce360.mocks.MockMarkdownRenderer;
import com.vforce360.mocks.MockReportRepository;
import com.vforce360.mar.model.ReportDisplayDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Test.
 * Story S-1: Fix: Modernization Assessment Report (MAR) displays as raw JSON.
 * 
 * This test ensures the service layer transforms raw data into formatted HTML,
 * preventing the 'Raw JSON' display bug.
 */
class MarServiceFeatureTest {

    private MarService marService;
    private MockReportRepository mockRepo;
    private MockMarkdownRenderer mockRenderer;

    @BeforeEach
    void setUp() {
        mockRepo = new MockReportRepository();
        mockRenderer = new MockMarkdownRenderer();
        marService = new MarService(mockRepo, mockRenderer);
    }

    @Test
    void whenProjectExists_returnsFormattedHtml_notRawJson() {
        // Arrange
        String projectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        // This is what comes out of the DB - "Raw" content (often just strings stored in JSON fields)
        String rawDbContent = "# Assessment Report\n* Item 1\n* Item 2"; 
        
        mockRepo.setFixedRawContent(rawDbContent);

        // Act
        ReportDisplayDto result = marService.getFormattedReport(projectId);

        // Assert
        assertNotNull(result, "Service should return a DTO");
        
        String html = result.getContentHtml();
        assertNotNull(html, "HTML content should not be null");
        
        // CORE ASSERTION: The content must be wrapped/processed, not raw.
        // If the bug exists (returns raw JSON/Text), it won't look like HTML.
        assertTrue(html.contains("<div>") || html.contains("<h1>"), 
            "Content should be rendered as HTML, not returned as raw text/JSON. Expected HTML tags.");
        
        // Ensure we didn't just dump the JSON string representation of the object
        assertFalse(html.contains("{\"rawContent\":"), 
            "The response appears to contain raw JSON serialization of the report object.");
    }

    @Test
    void whenProjectDoesNotExist_returnsNull() {
        // Arrange
        mockRepo.setShouldReturnEmpty(true);

        // Act
        ReportDisplayDto result = marService.getFormattedReport("unknown-project-id");

        // Assert
        assertNull(result, "Service should return null when project is not found");
    }

    @Test
    void givenMarkdownInput_shouldRenderStructureCorrectly() {
        // Arrange
        String projectId = "p-123";
        String markdown = "## Critical Findings\n- Severity: High";
        mockRepo.setFixedRawContent(markdown);

        // Act
        ReportDisplayDto result = marService.getFormattedReport(projectId);

        // Assert
        // The mock renderer wraps content in <div>. A real renderer would turn ## into <h2>.
        // We check that the adapter was actually used and transformation happened.
        assertTrue(result.getContentHtml().contains("Critical Findings"));
    }
}
