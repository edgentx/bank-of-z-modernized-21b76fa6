package com.vforce360.mar;

import com.vforce360.mar.ports.MarRepositoryPort;
import com.vforce360.mar.mocks.MockMarRepository;
import com.vforce360.mar.model.MarDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-End Regression test.
 * Verifies the HTTP Response returns Content-Type text/html, not application/json.
 */
@SpringBootTest
@AutoConfigureMockMvc
class MarControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // We use a MockBean here to isolate the web layer from the database,
    // simulating the 'Mock Adapter' pattern within the Spring context.
    @MockBean
    private MarRepositoryPort repository;

    @BeforeEach
    void setUp() {
        // Setup mock behavior for the regression scenario
        MarDocument doc = new MarDocument("21b76fa6-afb6-4593-9e1b-b5d7548ac4d1", 
            "Test Report", 
            "# Executive Summary\nThis is a critical defect.");
        when(repository.findByProjectId("21b76fa6-afb6-4593-9e1b-b5d7548ac4d1")).thenReturn(doc);
    }

    @Test
    void testMarReviewEndpointReturnsHtmlContentType() throws Exception {
        // Regression Test: "The MAR review section / report display no longer exhibits the reported behavior"
        // The reported behavior was raw JSON. We assert the Content-Type is HTML.
        
        mockMvc.perform(get("/api/mar/21b76fa6-afb6-4593-9e1b-b5d7548ac4d1")
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                // CRITICAL: Response must be HTML, not JSON
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                // The body should contain HTML tags, not raw JSON structures
                .andExpect(xpath("//html").exists());
    }
}