package com.vforce360.mar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vforce360.mar.ports.MarRendererPort;
import com.vforce360.mar.ports.MarRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * E2E Regression Test for S-1:
 * Modernization Assessment Report (MAR) displays as raw JSON instead of rendered markdown/HTML
 */
@SpringBootTest
@AutoConfigureMockMvc
public class MarReportDisplayE2ETest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper jsonMapper;

    @MockBean
    private MarRepositoryPort marRepositoryPort;

    @MockBean
    private MarRendererPort marRendererPort;

    @Test
    void marEndpointReturnsHtmlNotRawJson() throws Exception {
        // 1. Setup: Identify a brownfield project with a generated MAR
        UUID projectId = UUID.randomUUID();
        
        // Raw content stored in DB (JSON string)
        String rawMarJson = """
            {
                "projectId": "%s",
                "summary": "# Legacy Migration Strategy",
                "details": "- Refactor DB2 layer",
                "risk": "**High** complexity in CICS"
            }
            """.formatted(projectId);

        // Expected HTML content after rendering (simulating Markdown -> HTML)
        String expectedHtml = """
            <h1>Legacy Migration Strategy</h1>
            <ul>
            <li>Refactor DB2 layer</li>
            </ul>
            <p><strong>High</strong> complexity in CICS</p>
            """;

        // 2. Configure Mocks: The adapter finds the data and converts it
        when(marRepositoryPort.findByProjectId(projectId))
            .thenReturn(Optional.of(rawMarJson));
        
        when(marRendererPort.renderMarkdown(any(String.class)))
            .thenReturn(expectedHtml);

        // 3. Execute & Assert: Verify the API returns HTML
        // This FAILS in the defect state because it returns raw JSON application/json
        // It PASSES when the fix renders the content to text/html
        mvc.perform(get("/api/mar/" + projectId))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(content().string(containsString("Legacy Migration Strategy")))
            .andExpect(content().string(containsString("<h1>")))
            .andExpect(content().string(not(containsString("{\"projectId\"")))); // Assert NO raw JSON
    }

    @Test
    void marEndpointHandlesMissingDataGracefully() throws Exception {
        UUID missingProjectId = UUID.randomUUID();
        when(marRepositoryPort.findByProjectId(missingProjectId))
            .thenReturn(Optional.empty());

        mvc.perform(get("/api/mar/" + missingProjectId))
            .andExpect(status().isNotFound());
    }
}
