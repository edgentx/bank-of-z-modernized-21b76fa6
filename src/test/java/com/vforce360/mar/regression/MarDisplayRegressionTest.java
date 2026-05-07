package com.vforce360.mar.regression;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TDD Red Phase Test.
 * ID: S-1
 * Title: Fix: Modernization Assessment Report (MAR) displays as raw JSON instead of rendered markdown/HTML
 *
 * This test expects the API to return HTML content. 
 * Currently, the implementation returns raw JSON text (the defect).
 * This test will fail (Red) until the Controller is updated to process the Markdown.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MarDisplayRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testMarReviewSectionReturnsHtmlNotRawJson() throws Exception {
        UUID projectId = java.util.UUID.randomUUID();

        // Act: Request the MAR review section
        mockMvc.perform(get("/api/mar/" + projectId + "/review")
                .accept(MediaType.TEXT_HTML))
                // Expect: Status 200
                .andExpect(status().isOk())
                // Expect: Content Type is HTML (or a renderable format), NOT raw JSON application/json
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                // Expect: Content contains HTML tags (e.g., <h1>, <div>) indicating rendering occurred.
                // This assertion will fail because the current defect returns raw JSON like {"heading":...}
                .andExpect(content().string(containsString("<div")))
                // Expect: Content should NOT contain the raw JSON curl braces visible as text
                .andExpect(content().string( org.hamcrest.Matchers.not(containsString("\"heading\""))));
    }

    @Test
    void testMarReviewIsReadableFormattedDocument() throws Exception {
        UUID projectId = java.util.UUID.randomUUID();

        mockMvc.perform(get("/api/mar/" + projectId + "/review"))
                .andExpect(status().isOk())
                // Verify it is NOT just a plain text dump of JSON
                .andExpect(content().string( org.hamcrest.Matchers.not(containsString("{\"heading\": \"Assessment\"}"))));
    }
}
