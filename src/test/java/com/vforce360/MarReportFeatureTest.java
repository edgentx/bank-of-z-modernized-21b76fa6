package com.vforce360;

import com.vforce360.adapters.JpaMarReportAdapter;
import com.vforce360.adapters.MarkdownReportRendererAdapter;
import com.vforce360.model.ModernizationAssessmentReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Regression test for Story S-1.
 * Ensures the MAR report displays as HTML and not raw JSON.
 */
@SpringBootTest
@AutoConfigureMockMvc
class MarReportFeatureTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaMarReportAdapter reportStore;

    @Autowired
    private MarkdownReportRendererAdapter renderer;

    private static final String PROJECT_ID = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";

    @BeforeEach
    void setupData() {
        // Given: A report exists with Markdown content
        String markdownBody = "# Assessment\n\n* Issue 1: Legacy JSON\n* Issue 2: No HTML";
        ModernizationAssessmentReport report = new ModernizationAssessmentReport(
            PROJECT_ID,
            "Modernization Assessment Report: Z-Bank",
            markdownBody
        );
        reportStore.save(report);
    }

    @Test
    void testMarRendersAsHtmlNotJson() throws Exception {
        // When: We view the MAR review section
        mockMvc.perform(get("/api/mar/view/" + PROJECT_ID))
            // Then: Content is HTML
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", "text/html; charset=UTF-8"))
            // And: Contains rendered HTML headings (not raw # markdown)
            .andExpect(content().string(containsString("<h1>")))
            .andExpect(content().string(containsString("Modernization Assessment Report: Z-Bank")))
            // And: Markdown lists are converted to HTML lists
            .andExpect(content().string(containsString("<ul>")))
            .andExpect(content().string(containsString("<li>")))
            // And: Does NOT contain raw JSON brackets
            .andExpect(content().string( org.hamcrest.Matchers.not(containsString("{"))))
            .andExpect(content().string( org.hamcrest.Matchers.not(containsString("}"))));
    }

    @Test
    void testMarIncludesEscapedHtml() throws Exception {
        mockMvc.perform(get("/api/mar/view/" + PROJECT_ID))
            .andExpect(status().isOk())
            // Verify HTML entities are handled (XSS prevention)
            .andExpect(content().string(containsString("&lt;")));
    }
}
