package com.vforce360.e2e.regression;

import com.vforce360.MarReviewService;
import com.vforce360.model.ModernizationAssessmentReport;
import com.vforce360.ports.ModernizationAssessmentPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Regression Test for Defect: S-1
 * 'Modernization Assessment Report (MAR) displays as raw JSON instead of rendered markdown'
 * 
 * Location: e2e/regression/
 * This test ensures the specific reported behavior (raw JSON) is fixed.
 */
@SpringBootTest
class MarReviewRegressionTest {

    @Autowired
    private MarReviewService marReviewService; // Real service wiring

    @MockBean
    private ModernizationAssessmentPort assessmentPort; // Mocked infrastructure

    @Test
    void regressionS1_MarDisplaysAsFormattedText_notRawJson() {
        // ARRANGE
        // Simulating the exact data from the defect report context.
        String projectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        
        // The raw data stored in DB looks like this.
        Map<String, Object> rawDbContent = Map.of(
            "id", "MAR-101",
            "summary", "Refactor Mainframe",
            "status", "DRAFT"
        );
        
        ModernizationAssessmentReport rawReport = new ModernizationAssessmentReport(
            projectId, 
            "Assessment 2024", 
            rawDbContent, 
            "DRAFT"
        );

        when(assessmentPort.findByProjectId(projectId)).thenReturn(rawReport);

        // ACT
        // The system response. In the defect, this returned raw JSON string.
        String response = marReviewService.getMarForReview(projectId);

        // ASSERT
        // 1. Regression Guard: The response must NOT be raw JSON.
        // We check for characters that indicate a Map.toString() or JSON serialization.
        // rawDbContent.toString() produces "{id=MAR-101, summary=Refactor...}"
        assertFalse(response.contains("{"), "Regression Check: Response should not contain raw JSON braces");
        assertFalse(response.contains("="), "Regression Check: Response should not contain Map entry separators");
        assertFalse(response.contains("@"), "Regression Check: Response should not be a Java Object reference");

        // 2. Positive Assertion: It must look like formatted text (Markdown/HTML).
        assertTrue(response.length() > 0, "Response should not be empty");
    }
}
