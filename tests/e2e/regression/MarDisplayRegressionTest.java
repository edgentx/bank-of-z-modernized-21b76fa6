package com.vforce360.e2e.regression;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.vforce360.ports.MarPort;

/**
 * End-to-End Regression Test.
 * Verifies the HTTP response layer to ensure the JSON defect is fixed for the CEO view.
 */
@SpringBootTest
@AutoConfigureMockMvc
class MarDisplayRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MarPort marPort; // Mocking the database adapter

    @Test
    @DisplayName("Regression: MAR endpoint returns HTML, not raw JSON (CEO Review)")
    void testMarReviewEndpointReturnsHtml() throws Exception {
        // Arrange: The 'Database' returns raw JSON
        String projectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        String rawDbContent = "{\"summary\": \"Urgent Refactor Needed\", \"risk\": \"High\"}";
        
        when(marPort.getMarContent(projectId)).thenReturn(rawDbContent);

        // Act & Assert: The API should return processed HTML
        mockMvc.perform(get("/api/projects/{projectId}/mar", projectId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html")) // Expecting HTML, not JSON
                .andExpect(xpath("//html").exists())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Urgent Refactor Needed")))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("{\"summary\"")))); // Assert raw JSON syntax is gone
    }
}
