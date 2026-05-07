package com.vforce360.app.services;

import com.vforce360.app.models.ReportDisplayModel;
import com.vforce360.shared.ports.MarkdownRendererPort;
import com.vforce360.shared.ports.ModernizationReportPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * TDD Red Phase Test.
 * 
 * Context: The MAR (Modernization Assessment Report) is stored as a JSON blob in the database.
 * The 'content' field contains Markdown text.
 * 
 * Current Bug: The system displays the raw JSON string instead of extracting the markdown and rendering it.
 * 
 * This test will fail against the current implementation because the current implementation
 * likely passes the raw JSON string directly to the renderer/view.
 */
@ExtendWith(MockitoExtension.class)
class ReportServiceRedPhaseTest {

    @Mock
    private ModernizationReportPort reportPort;

    @Mock
    private MarkdownRendererPort rendererPort;

    @InjectMocks
    private ReportService reportService;

    private static final String PROJECT_ID = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";

    /**
     * AC: The MAR review section / report display no longer exhibits the reported behavior
     * 
     * Scenario: Valid project with JSON payload containing Markdown.
     * Expected: The returned HTML should be the RENDERED markdown, not the raw JSON.
     */
    @Test
    void whenDatabaseReturnsJson_ShouldExtractMarkdownAndRenderHtml() {
        // 1. Setup Data (Simulating DB2/MongoDB return value)
        String rawJsonFromDb = "{\"title\":\"Assessment\",\"content\":\"# Critical Findings\\n- Item 1\\n- Item 2\"}";
        String expectedMarkdown = "# Critical Findings\n- Item 1\n- Item 2";
        String expectedHtml = "<h1>Critical Findings</h1><ul><li>Item 1</li><li>Item 2</li></ul>";

        // 2. Configure Mocks
        when(reportPort.findRawContentByProjectId(PROJECT_ID)).thenReturn(Optional.of(rawJsonFromDb));
        // We assume the renderer works correctly if it gets valid markdown
        when(rendererPort.renderToHtml(expectedMarkdown)).thenReturn(expectedHtml);

        // 3. Execute Service
        Optional<ReportDisplayModel> result = reportService.getReportForDisplay(PROJECT_ID);

        // 4. Assertions (Red Phase: This will fail until Service is fixed)
        assertThat(result).isPresent();
        
        // THE CRITICAL ASSERTION: 
        // The HTML must NOT contain JSON structural characters like {, ", : as raw text.
        // It must contain the HEADER.
        String html = result.get().getRenderedHtml();
        
        assertThat(html).contains("<h1>Critical Findings</h1>");
        assertThat(html).doesNotContain("{\"content\""); 
        assertThat(html).doesNotContain(":"); 
        
        // Also verify that the renderer was called with the extracted markdown, 
        // NOT the raw JSON string.
        // (In a real scenario, we might use ArgumentCaptor, but here we check the output flow)
    }

    /**
     * Regression Test / Edge Case
     * Scenario: Database returns raw markdown directly (legacy format), not JSON.
     * System should still handle it gracefully.
     */
    @Test
    void whenDatabaseReturnsRawMarkdown_ShouldRenderDirectly() {
        String rawMarkdown = "# Legacy Report\\nJust text.";
        String expectedHtml = "<h1>Legacy Report</h1><p>Just text.</p>";

        when(reportPort.findRawContentByProjectId(PROJECT_ID)).thenReturn(Optional.of(rawMarkdown));
        when(rendererPort.renderToHtml(rawMarkdown)).thenReturn(expectedHtml);

        Optional<ReportDisplayModel> result = reportService.getReportForDisplay(PROJECT_ID);

        assertThat(result).isPresent();
        assertThat(result.get().getRenderedHtml()).isEqualTo(expectedHtml);
    }

    /**
     * Regression Test / Edge Case
     * Scenario: Project not found.
     */
    @Test
    void whenProjectNotFound_ShouldReturnEmpty() {
        when(reportPort.findRawContentByProjectId("unknown-id")).thenReturn(Optional.empty());

        Optional<ReportDisplayModel> result = reportService.getReportForDisplay("unknown-id");

        assertThat(result).isEmpty();
    }
}
