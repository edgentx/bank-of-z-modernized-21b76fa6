package com.vforce360;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.vforce360.model.MarReport;
import com.vforce360.ports.MarReportPort;
import com.vforce360.ports.MarkdownRendererPort;

/**
 * Controller for viewing the Modernization Assessment Report.
 * Renders the report as HTML.
 */
@Controller
public class MarReportController {

    private final MarReportPort marReportPort;
    private final MarkdownRendererPort markdownRendererPort;

    public MarReportController(MarReportPort marReportPort, MarkdownRendererPort markdownRendererPort) {
        this.marReportPort = marReportPort;
        this.markdownRendererPort = markdownRendererPort;
    }

    @GetMapping("/projects/{projectId}/mar/review")
    public ResponseEntity<String> viewMarReport(@PathVariable String projectId) {
        // Fetch the report (raw JSON structure)
        MarReport report = marReportPort.findByProjectId(projectId);
        
        if (report == null) {
            return ResponseEntity.notFound().build();
        }

        // FIX: Render the 'rawContent' field content to HTML instead of returning raw JSON
        String renderedHtml = markdownRendererPort.renderToHtml(report.getRawContent());
        
        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.TEXT_HTML)
                .body(renderedHtml);
    }
}
