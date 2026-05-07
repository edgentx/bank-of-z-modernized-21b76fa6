package com.vforce360.service;

import com.vforce360.model.ModernizationAssessmentReport;
import com.vforce360.ports.MarRepositoryPort;
import com.vforce360.mocks.MockMarRepositoryPort;
import com.vforce360.mocks.MockReportRendererPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * RED PHASE TEST
 * 
 * These tests verify the business logic required to fix the defect:
 * "Modernization Assessment Report (MAR) displays as raw JSON instead of rendered markdown/HTML"
 * 
 * The tests will FAIL initially because:
 * 1. The MarService class may not exist.
 * 2. The getMarHtml method is not implemented.
 * 3. The logic to extract markdown and call the renderer is missing.
 */
class MarServiceTest {

    private MarService marService;
    private MockMarRepositoryPort mockRepository;
    private MockReportRendererPort mockRenderer;

    @BeforeEach
    void setUp() {
        mockRepository = new MockMarRepositoryPort();
        mockRenderer = new MockReportRendererPort();
        
        // Instantiate the Service Under Test (SUT)
        // This will fail to compile until MarService is created with this constructor
        marService = new MarService(mockRepository, mockRenderer);
    }

    @Test
    void getMarHtml_shouldReturnRenderedContent_whenReportExists() {
        // ARRANGE
        String projectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        String rawMarkdown = "# Assessment\n\n* Critical Risk found.";
        String expectedHtmlFragment = "<h1>Assessment</h1>"; // Implementation will use a real renderer

        ModernizationAssessmentReport report = new ModernizationAssessmentReport(projectId, rawMarkdown, "GENERATED");
        mockRepository.saveReport(projectId, report);

        // ACT
        // This method should: 1. Fetch Entity 2. Get Markdown 3. Render to HTML
        String result = marService.getMarHtml(projectId);

        // ASSERT
        assertNotNull(result, "Service should return HTML content");
        
        // Verify that it isn't the raw JSON-like Markdown string
        assertNotEquals(rawMarkdown, result, "Returned content should NOT be raw markdown");
        
        // Verify it contains HTML tags
        assertTrue(result.contains("<") && result.contains(">"), "Returned content should contain HTML tags");
        
        // Verify it contains structured content
        assertTrue(result.contains("Assessment") || result.contains("Risk"), "Content should include report text");
    }

    @Test
    void getMarHtml_shouldThrowException_whenReportNotFound() {
        // ARRANGE
        String projectId = "non-existent-project";
        // Repository returns null for this ID

        // ACT & ASSERT
        Exception exception = assertThrows(RuntimeException.class, () -> {
            marService.getMarHtml(projectId);
        });

        assertTrue(exception.getMessage().contains("not found") || exception.getMessage().contains("MAR"));
    }

    @Test
    void getMarHtml_shouldNotReturnRawJsonString() {
        // Regression test for the specific defect
        // ARRANGE
        String projectId = "project-with-json-like-content";
        // Simulate a report that might have been stored as JSON string previously or has code blocks
        String contentThatLooksLikeJson = "{\"risk\": \"high\"}"; 
        ModernizationAssessmentReport report = new ModernizationAssessmentReport(projectId, contentThatLooksLikeJson, "GENERATED");
        mockRepository.saveReport(projectId, report);

        // ACT
        String result = marService.getMarHtml(projectId);

        // ASSERT
        // The defect was that it showed raw JSON. We expect HTML now.
        assertFalse(result.equals(contentThatLooksLikeJson), "Should not return the raw string content directly");
        assertTrue(result.contains("<"), "Should be wrapped in HTML");
    }
}