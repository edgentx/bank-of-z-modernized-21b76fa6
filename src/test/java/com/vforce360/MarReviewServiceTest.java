package com.vforce360;

import com.vforce360.model.ModernizationAssessmentReport;
import com.vforce360.ports.ModernizationAssessmentPort;
import com.vforce360.ports.ReportRendererPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

/**
 * TDD Red Phase Test.
 * 
 * Context: The Modernization Assessment Report (MAR) displays as raw JSON.
 * Expected: It should display as rendered Markdown/HTML.
 * 
 * This test suite validates that the Service layer fetches raw data AND
 * explicitly invokes a renderer, transforming the data away from raw JSON.
 */
@SpringBootTest
class MarReviewServiceTest {

    // The Port (Interface) we are testing against.
    // In the real app, this is wired to the Controller/Service.
    // We mock the dependencies to ensure isolation.
    @Autowired
    private MarReviewService marReviewService;

    @MockBean
    private ModernizationAssessmentPort assessmentPort;

    @MockBean
    private ReportRendererPort rendererPort;

    private ModernizationAssessmentReport rawReport;

    @BeforeEach
    void setUp() {
        // Constructing the 'Raw JSON' data structure that was causing the defect.
        Map<String, Object> rawJsonData = Map.of(
            "assessmentId", "MAR-2024-X",
            "riskScore", 85,
            "summary", "System requires immediate refactoring.",
            "details", Map.of(
                "language", "COBOL",
                "linesOfCode", 500000
            )
        );

        rawReport = new ModernizationAssessmentReport(
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1",
            "Legacy System Assessment",
            rawJsonData,
            "PENDING_REVIEW"
        );
    }

    @Test
    void testServiceReturnsFormattedMarkdown_notRawJson() {
        // GIVEN
        String projectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        
        // The port returns the raw object (JSON structure)
        when(assessmentPort.findByProjectId(projectId)).thenReturn(rawReport);

        // The renderer should convert that object to Markdown
        // We mock the return value to simulate what a real Markdown renderer does.
        // Crucially, the result should NOT look like a JSON object string.
        String formattedMarkdown = "# Legacy System Assessment\n\n**Risk Score:** 85\n\nSystem requires immediate refactoring.";
        when(rendererPort.toMarkdown(rawReport)).thenReturn(formattedMarkdown);

        // WHEN
        // We assume the Service layer has a method 'getMarForReview' that returns a String (or DTO)
        // For TDD Red phase, we are defining the API we WANT.
        String actualResult = marReviewService.getMarForReview(projectId);

        // THEN
        // 1. Verify the renderer was actually called (Defect: it was bypassed)
        verify(rendererPort).toMarkdown(rawReport);

        // 2. The result must be the formatted Markdown, NOT the raw JSON structure.
        assertEquals(formattedMarkdown, actualResult);

        // 3. Explicitly assert that the result does NOT contain JSON delimiters.
        // If the defect exists, the result might be "{..." or a class hash.
        assertFalse(actualResult.contains("{"), "Result should not start with raw JSON brace");
        assertFalse(actualResult.contains("="), "Result should not contain Java Map toString '='");
    }

    @Test
    void testServiceReturnsHtml_whenHtmlRequested() {
        // GIVEN
        String projectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        when(assessmentPort.findByProjectId(projectId)).thenReturn(rawReport);

        String expectedHtml = "<div class='mar'>Legacy System Assessment</div>";
        when(rendererPort.toHtml(rawReport)).thenReturn(expectedHtml);

        // WHEN
        String actualResult = marReviewService.getMarHtml(projectId);

        // THEN
        verify(rendererPort).toHtml(rawReport);
        assertEquals(expectedHtml, actualResult);
        assertTrue(actualResult.contains("<"), "Result should be HTML content");
    }
}
