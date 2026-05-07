package com.vforce360.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vforce360.model.ReportIdentifier;
import com.vforce360.ports.IModernizationReportRepository;
import com.vforce360.service.ReportService;
import com.vforce360.tests.mocks.MockModernizationReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * TDD Red Phase Tests.
 *
 * Story S-1: Fix: Modernization Assessment Report (MAR) displays as raw JSON
 * instead of rendered markdown/HTML.
 *
 * These tests will fail against the current defective implementation which
 * returns raw JSON strings.
 */
@DisplayName("Modernization Assessment Report (S-1)")
class ModernizationAssessmentReportTest {

    private MockModernizationReportRepository mockRepo;
    private ReportService reportService;
    private ObjectMapper jsonMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockRepo = new MockModernizationReportRepository();
        reportService = new ReportService(mockRepo);
    }

    @Nested
    @DisplayName("Given a project with a generated Modernization Assessment Report")
    class ContextReportExists {

        private static final String PROJECT_ID = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        private final String rawJsonContent = String.format(
                "{\"projectId\":\"%s\",\"title\":\"Assessment\",\"summary\":\"Critical issues found\"}",
                PROJECT_ID);

        @BeforeEach
        void seedData() {
            // Simulating the database state where content is stored as raw JSON string
            mockRepo.givenRawContentIs(rawJsonContent);
        }

        @Test
        @DisplayName("When the MAR review section is viewed, Then content should not be raw JSON")
        void testMarNotRawJson() {
            String result = reportService.getMarDisplayContent(PROJECT_ID);

            // Current Behavior (Bug): Returns the raw JSON string.
            // Expected Behavior: Should be processed/markdown.
            // This assertion checks that the service has successfully transformed the data.
            assertFalse(
                    isRawJson(result),
                    "Content should not be raw JSON. Expected formatted content."
            );
        }

        @Test
        @DisplayName("When the MAR review section is viewed, Then it should display as formatted markdown")
        void testMarIsFormattedMarkdown() {
            String result = reportService.getMarDisplayContent(PROJECT_ID);

            // Heuristic checks for 'Formatted Markdown/HTML' vs raw JSON.
            // A raw JSON object starts with '{'. A formatted document usually starts with text or '<'.
            assertFalse(
                    result.trim().startsWith("{"),
                    "Document should not start with the JSON object character '{'."
            );
            assertFalse(
                    result.trim().startsWith("\""),
                    "Document should not start with the JSON string character '\"'."
            );

            // Assuming the fix wraps content in HTML or converts Markdown to HTML,
            // we look for HTML tags or specific structural text.
            // For the purpose of TDD red-phase, we assert the absence of the bug pattern (JSON keys).
            assertFalse(
                    result.contains(\"\\\"projectId\\\"\"),
                    "Result should not contain raw JSON key-value pairs visible to the user."
            );
        }

        @Test
        @DisplayName("Regression: Verify the specific project from the defect report is handled")
        void testRegressionSpecificProject() {
            // Explicit check for the project ID mentioned in the story
            String result = reportService.getMarDisplayContent(PROJECT_ID);

            assertNotNull(result);
            // The CEO needs to read this.
            assertFalse(result.equals("{}"));
            assertFalse(result.equals("[]"));
        }
    }

    @Nested
    @DisplayName("Given a project with a complex nested MAR")
    class ComplexNestedJson {

        private static final String PROJECT_ID = "proj-nested";
        private final String nestedJson = "{\"assessment\":{\"risk\":\"high\",\"details\":[{\"id\":1},{\"id\":2}]}}";

        @BeforeEach
        void seedData() {
            mockRepo.givenRawContentIs(nestedJson);
        }

        @Test
        @DisplayName("Then the output should be a clean, formatted document, not a JSON tree")
        void testComplexJsonIsProcessed() {
            String result = reportService.getMarDisplayContent(PROJECT_ID);

            // If it were raw JSON, it would look like {"assessment":{"risk":...}}
            // We expect HTML headers or Markdown headers (e.g., # Risk Assessment)
            assertFalse(result.contains("{\"assessment\":{"), "Deeply nested JSON should be flattened/processed.");
        }
    }

    // --- Helpers ---

    private boolean isRawJson(String input) {
        try {
            jsonMapper.readTree(input);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }
}
