package com.vforce360;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vforce360.ports.ReportData;
import com.vforce360.ports.ReportIdentifier;
import com.vforce360.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Regression test for Story S-1:
 * Modernization Assessment Report (MAR) displays as raw JSON instead of rendered markdown/HTML.
 *
 * Location: tests/test/java/com/vforce360/
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ModernizationAssessmentReportTest {

    public static final String PROJECT_ID = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper jsonMapper;

    @Autowired
    private ReportService reportService;

    /**
     * Service Layer Unit Test (Red Phase).
     * Verifies that the ReportService transforms raw JSON data into formatted Markdown.
     */
    @Test
    void testServiceTransformsRawJsonToMarkdown() throws Exception {
        // 1. Setup raw JSON data (simulating database fetch)
        String rawJson = "{\"projectId\": \"" + PROJECT_ID + "\", \"status\": \"DRAFT\", \"summary\": \"Legacy monolith\"}";

        // 2. Call the service method to get formatted content
        // Note: This method does not exist yet on ReportService
        String formattedContent = reportService.getFormattedReport(PROJECT_ID);

        // 3. Assertions
        // The content should NOT look like raw JSON
        if (formattedContent.contains("{\"") || formattedContent.contains("\"projectId\" : ")) {
            throw new AssertionError("Report content appears to be raw JSON. Expected formatted Markdown.");
        }

        // The content should contain Markdown elements
        if (!formattedContent.contains("#") && !formattedContent.contains("*")) {
            throw new AssertionError("Report content does not appear to contain Markdown formatting (headings or bullets).");
        }
    }

    /**
     * Integration Test (Red Phase).
     * Verifies the HTTP GET endpoint returns rendered HTML, not raw JSON text.
     */
    @Test
    void testMarReviewEndpointReturnsHtmlNotJson() throws Exception {
        mvc.perform(get("/api/projects/" + PROJECT_ID + "/mar/review")
                        .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                // Content Type must be HTML, not JSON
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                // CRITICAL: The response body must not contain the raw JSON string "\"projectId\"
                .andExpect(content().string(not(containsString("\"projectId\""))))
                .andExpect(content().string(not(containsString("{\""))))
                // Expected: It should contain readable text or HTML tags
                .andExpect(content().string(containsString("<"))) // e.g. <h1> or <div>
                .andExpect(content().string(containsString(PROJECT_ID))); // Should display the ID somewhere
    }
}
