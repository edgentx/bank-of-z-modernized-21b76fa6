package com.vforce360.mar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vforce360.mar.domain.ModernizationAssessmentReport;
import com.vforce360.mar.ports.MarRepositoryPort;
import com.vforce360.mocks.MockMarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * E2E Regression Test for Story S-1.
 * 
 * Tests the contract between the Controller and the hypothetical Rendering layer.
 * Since the real implementation of the render endpoint does not exist yet, 
 * we expect a 404 Not Found (Red Phase), OR we verify that if the endpoint exists,
 * it does NOT return raw JSON.
 * 
 * Here we assert the "Not Raw JSON" behavior on the endpoint /api/mar/{id}/review
 */
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MarDisplayRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    // We can autowire the mock if defined as a @Bean in TestConfig, 
    // or manually set it up here. For simplicity, we'll assume a test config 
    // or just use the MockMvc to hit the endpoint.
    // However, to ensure data state, we inject the Port.
    @Autowired(required = false)
    private MarRepositoryPort marRepositoryPort;

    private final UUID projectId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        // Setup Mock Data using the Port
        if (marRepositoryPort instanceof MockMarRepository) {
            MockMarRepository mockRepo = (MockMarRepository) marRepositoryPort;
            
            // Create a report that contains Markdown-like content
            // This simulates the "Expected" state of the database
            ModernizationAssessmentReport report = new ModernizationAssessmentReport();
            report.setProjectId(projectId);
            report.setRawContent("# Assessment Summary\n\n* Finding 1: Legacy COBOL\n* Finding 2: Mainframe Deps");
            
            mockRepo.addProjectReport(projectId, report);
        }
    }

    @Test
    void shouldNotDisplayRawJsonInMarReviewSection() throws Exception {
        // URL for the MAR review section (frontend API)
        String url = "/api/projects/" + projectId + "/mar/review";

        // ACT & ASSERT
        // We perform the request. 
        // Ideally, this endpoint exists and returns HTML or Rendered content.
        // We use containsString to ensure the Response Body is NOT a JSON object string.
        
        mockMvc.perform(get(url)
                .accept(MediaType.TEXT_HTML_VALUE))
                .andExpect(status().isOk()) // Asserting endpoint exists (Red phase if 404)
                .andExpect(content().string(containsString("Assessment Summary"))) // Should contain rendered text
                .andExpect(content().string(containsString("<h1>"))) // Should contain HTML tags
                .andExpect(content().string(containsString("<ul>"))); // Should contain list tags

        // Explicit check that it does not look like raw JSON
        mockMvc.perform(get(url).accept(MediaType.TEXT_HTML_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string( org.hamcrest.Matchers.not(containsString("\"rawContent\"")) ))
                .andExpect(content().string( org.hamcrest.Matchers.not(containsString("{\"projectId\"")) ));
    }

    @Test
    void shouldRenderMarkdownHeadingsCorrectly() throws Exception {
        String url = "/api/projects/" + projectId + "/mar/review";

        // Assert that # Assessment Summary becomes <h1>Assessment Summary</h1>
        mockMvc.perform(get(url)
                .accept(MediaType.TEXT_HTML_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<h1>Assessment Summary</h1>")));
    }
}
