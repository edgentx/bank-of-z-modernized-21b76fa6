package com.vforce360;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vforce360.ports.MarReportPort;
import mocks.MockMarReportAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration/Regression test for Story S-1.
 * Verifies that the MAR report display returns rendered HTML/Markdown, not raw JSON.
 * 
 * Context: Real endpoint, Mocked Adapter.
 */
@SpringBootTest(classes = MarReportIntegrationTest.TestConfig.class)
@AutoConfigureMockMvc
class MarReportIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    // The system under test uses the Port, we provide the Mock via TestConfig
    
    @TestConfiguration
    static class TestConfig {
        @Bean
        public MarReportPort marReportPort() {
            return new MockMarReportAdapter();
        }
        
        @Bean
        public MarReportController marReportController(MarReportPort port) {
            return new MarReportController(port);
        }
    }

    /**
     * Test Case: Verify MAR display renders content correctly.
     * 
     * Scenario: User navigates to brownfield project MAR review section.
     * Expected: Content is formatted HTML/Markdown.
     * Actual (Bug): Content is raw JSON string.
     */
    @Test
    void whenMarRequested_thenReturnRenderedHtmlNotRawJson() throws Exception {
        String projectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";

        // Perform the GET request to the review section
        mvc.perform(get("/api/projects/" + projectId + "/mar/review"))
           // Expect HTTP 200
           .andExpect(status().isOk())
           // Expect Content-Type to be HTML, not JSON
           .andExpect(content().contentTypeCompatibleWith("text/html"))
           // Verify the body is NOT the raw JSON string
           .andExpect(jsonPath("$.content").isString())
           // CRITICAL ASSERTION: The content should contain HTML tags 
           // (simulating the "clean, formatted document" requirement)
           .andExpect(jsonPath("$.content", containsString("<")))
           // CRITICAL ASSERTION: The content should NOT look like raw JSON object notation
           .andExpect(jsonPath("$.content", not(startsWith("{"))));
    }

    /**
     * Test Case: Verify structural response wrapper.
     */
    @Test
    void whenMarRequested_responseStructureIsCorrect() throws Exception {
        String projectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";

        mvc.perform(get("/api/projects/" + projectId + "/mar/review"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.contentType").exists())
           .andExpect(jsonPath("$.content").exists());
    }
}
