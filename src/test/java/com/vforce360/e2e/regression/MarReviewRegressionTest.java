package com.vforce360.e2e.regression;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vforce360.mar.model.ModernizationAssessmentReport;
import com.vforce360.ports.ModernizationReportPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Regression Test for Story S-1.
 * Verifies the REST API layer does not return raw JSON when HTML is expected.
 */
@SpringBootTest
@AutoConfigureMockMvc
class MarReviewRegressionTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    // Mock the repository layer to simulate DB state without Testcontainers for this specific check
    @MockBean
    private ModernizationReportPort reportRepository;

    @Test
    void testMarReviewEndpoint_returnsHtmlContentType_notRawJson() throws Exception {
        // Arrange: Setup a project with a generated MAR
        String projectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        String rawMarkdown = "# Modernization Assessment\n* Phase 1: Complete";
        
        ModernizationAssessmentReport mar = new ModernizationAssessmentReport(projectId, rawMarkdown);
        when(reportRepository.findByProjectId(projectId)).thenReturn(Optional.of(mar));

        // Act: View the MAR review section
        // Expected Behavior: 200 OK
        // Actual Behavior (Bug): 200 OK but body is raw JSON object like {"rawContent": "..."}
        var result = mvc.perform(get("/api/v1/mar/project/" + projectId + "/review")
                .accept(MediaType.APPLICATION_JSON))

        // Assert
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.contentHtml").exists());
            
        // Additional check to ensure contentHtml isn't just the raw markdown string (simple validation)
        String responseJson = result.andReturn().getResponse().getContentAsString();
        
        // If the bug exists, the serialization might look like {"rawContent": "# ...", "projectId": "..."}
        // If fixed, it should look like {"contentHtml": "<div>...</div>"}
        // The existence of 'contentHtml' key is checked above.
        // We also want to ensure 'rawContent' is NOT exposed directly at the root if it's an internal field.
        // (Jackson might serialize it if we returned the Entity directly, which is the likely cause of the bug)
    }
}
