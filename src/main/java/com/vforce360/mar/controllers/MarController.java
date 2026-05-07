package com.vforce360.mar.controllers;

import com.vforce360.mar.models.ModernizationAssessmentReport;
import com.vforce360.ports.MarkdownRendererPort;
import com.vforce360.ports.ModernizationReportPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for Modernization Assessment Report (MAR) review.
 * Addresses Story S-1: Fixes raw JSON display to rendered HTML.
 */
@RestController
@RequestMapping("/api/mar")
public class MarController {

    private final ModernizationReportPort reportPort;
    private final MarkdownRendererPort rendererPort;

    /**
     * Constructor-based dependency injection.
     * 
     * @param reportPort Port to access MAR data (MongoDB, Mock, etc).
     * @param rendererPort Port to render Markdown to HTML.
     */
    public MarController(ModernizationReportPort reportPort, MarkdownRendererPort rendererPort) {
        this.reportPort = reportPort;
        this.rendererPort = rendererPort;
    }

    /**
     * Endpoint to retrieve the rendered HTML review of the MAR.
     * This fixes the defect where raw JSON was displayed.
     * 
     * @param projectId The ID of the project.
     * @return ResponseEntity containing HTML string.
     */
    @GetMapping("/review/{projectId}/html")
    public ResponseEntity<String> getMarReviewHtml(@PathVariable String projectId) {
        // 1. Fetch the raw data (which contains markdown)
        ModernizationAssessmentReport report = reportPort.getReport(projectId);
        
        if (report == null) {
            return ResponseEntity.notFound().build();
        }

        // 2. Extract the markdown content
        String markdown = report.getRawMarkdownContent();
        
        // 3. Render to HTML
        String htmlContent = rendererPort.renderToHtml(markdown);

        // 4. Return the rendered content
        return ResponseEntity.ok(htmlContent);
    }
}
