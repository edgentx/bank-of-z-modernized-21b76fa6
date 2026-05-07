package com.vforce360;

import com.vforce360.adapters.MarkdownReportRendererAdapter;
import com.vforce360.model.ModernizationAssessmentReport;
import com.vforce360.ports.MarReportPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for serving the Modernization Assessment Report.
 * FIXED: Corrected Port injection and method chaining.
 */
@RestController
@RequestMapping("/api/mar")
public class MarReportController {

    private final MarReportPort marReportPort;
    private final MarkdownReportRendererAdapter renderer;

    public MarReportController(MarReportPort marReportPort, MarkdownReportRendererAdapter renderer) {
        this.marReportPort = marReportPort;
        this.renderer = renderer;
    }

    @GetMapping("/view/{projectId}")
    public ResponseEntity<String> getMarView(@PathVariable String projectId) {
        // 1. Fetch Report
        // FIXED: Added 'findByProjectId' to Port interface and implemented it in Jpa adapter
        return marReportPort.findByProjectId(projectId)
                .map(report -> {
                    // 2. Render to HTML (Fix for the defect: JSON -> HTML)
                    String htmlContent = renderer.renderReportToHtml(report);
                    return ResponseEntity.ok()
                            .header("Content-Type", "text/html; charset=UTF-8")
                            .body(htmlContent);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}