package com.vforce360;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vforce360.ports.IModernizationReportRepository;
import com.vforce360.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * TDD Red Phase Tests for Story S-1.
 * Verifies that raw JSON content stored in the repository is converted
 * into readable Markdown format for display.
 */
class ModernizationAssessmentReportTest {

    @Mock
    private IModernizationReportRepository repository;

    private ReportService reportService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reportService = new ReportService(repository, objectMapper);
    }

    @Test
    void testReportDisplaysFormattedMarkdown_notRawJson() throws Exception {
        // Arrange
        String projectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        String rawJsonContent = "{\"projectId\":\"" + projectId + "\",\"assessment\":{\"title\":\"Legacy Migration\",\"risk\":\"High\"}}";

        // Simulate repository returning raw JSON string
        when(repository.findRawContentByProjectId(projectId))
            .thenReturn(Optional.of(rawJsonContent));

        // Act
        String renderedContent = reportService.getFormattedReport(projectId);

        // Assert
        assertNotNull(renderedContent, "Content should not be null");

        // CRITICAL: Verify it is NOT displaying as raw JSON
        assertFalse(renderedContent.trim().startsWith("{"), "Output should not start with raw JSON brace");
        assertFalse(renderedContent.contains("\"projectId\":"), "Output should not contain raw JSON keys");
        assertFalse(renderedContent.contains("\"title\":"), "Output should not contain raw JSON property names");

        // Verify it IS displaying as formatted Markdown
        // Expected conversion logic would turn JSON keys into Headers (#)
        assertTrue(renderedContent.contains("#"), "Output should contain Markdown headers derived from JSON keys");
        assertTrue(renderedContent.contains("Legacy Migration"), "Output should contain readable text values");
    }

    @Test
    void testReportHandlesInvalidJson_gracefully() throws Exception {
        // Arrange
        String projectId = "invalid-json-project";
        String badContent = "This is not valid JSON at all.";

        when(repository.findRawContentByProjectId(projectId))
            .thenReturn(Optional.of(badContent));

        // Act
        String result = reportService.getFormattedReport(projectId);

        // Assert
        assertNotNull(result);
        // Ideally, it should wrap in a code block or escape it, not throw an exception
        assertTrue(result.contains("This is not valid JSON") || result.contains("```"),
            "Invalid JSON should be wrapped in code blocks or handled gracefully");
    }

    @Test
    void testReportHandlesMissingContent() {
        // Arrange
        String projectId = "non-existent-project";
        when(repository.findRawContentByProjectId(projectId))
            .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            reportService.getFormattedReport(projectId);
        }, "Service should throw an error or return empty when content is missing");
    }

    @Test
    void testReportParsesComplexStructure() throws Exception {
        // Arrange
        String projectId = "complex-project";
        String json = "{\"report\":{\"summary\":\"Critical\",\"details\":[\"Item 1\",\"Item 2\"]}}";
        
        when(repository.findRawContentByProjectId(projectId))
            .thenReturn(Optional.of(json));

        // Act
        String result = reportService.getFormattedReport(projectId);

        // Assert
        assertFalse(result.contains("\"summary\""), "Should hide JSON keys");
        assertTrue(result.contains("Critical"), "Should display values");
        // Verify lists become bullets
        assertTrue(result.contains("-") || result.contains("*"), "Arrays should be converted to bullet points");
    }
}
