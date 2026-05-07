package com.vforce360;

import com.vforce360.adapters.ReportController;
import com.vforce360.domain.ModernizationAssessmentReport;
import com.vforce360.ports.ModernizationReportPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TDD Red Phase Test.
 * Regression test for Story S-1: MAR displays as raw JSON instead of rendered document.
 *
 * Expected Behavior:
 * The API should return a structured JSON object (Content-Type: application/json).
 * The Frontend (Next.js) will then render this object.
 * If the API returns a String containing raw JSON text, the frontend displays text.
 * This test ensures the Controller returns a structured Object, not a String.
 */
@WebMvcTest(ReportController.class)
class MarReviewRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ModernizationReportPort reportPort;

    private static final String PROJECT_ID = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";

    @BeforeEach
    void setUp() {
        // Define the structured data that should be returned.
        // This simulates the data fetched from the 'database'.
        ModernizationAssessmentReport report = new ModernizationAssessmentReport(
                PROJECT_ID,
                "Legacy Mainframe Modernization Assessment",
                "This document outlines the strategic path forward for the COBOL ledger system.",
                List.of("High technical debt in IMS/TM", "CICS transactions are candidates for microservice extraction"),
                Map.of("complexity", "High", "risk", "Medium")
        );

        when(reportPort.getReport(PROJECT_ID)).thenReturn(report);
    }

    @Test
    void whenProjectMarRequested_thenReturnsStructuredJsonNotRawText() throws Exception {
        // The defect was that the screen showed raw JSON.
        // This implies the Content-Type might have been text/plain or the body was a Stringified JSON.
        // We verify we get proper JSON content type.

        mockMvc.perform(get("/api/projects/" + PROJECT_ID + "/mar"))
                .andExpect(status().isOk())
                // CRITICAL CHECK: Verify Content-Type is JSON, ensuring the frontend treats it as an object to render,
                // not a text string to display verbatim.
                .andExpect(content().contentType("application/json"))
                // Verify the structure is parsed correctly, not a raw string.
                .andExpect(jsonPath("$.projectId").value(PROJECT_ID))
                .andExpect(jsonPath("$.title").value("Legacy Mainframe Modernization Assessment"))
                .andExpect(jsonPath("$.executiveSummary").exists())
                .andExpect(jsonPath("$.keyFindings").isArray())
                .andExpect(jsonPath("$.technicalMetrics").isMap());
    }
}