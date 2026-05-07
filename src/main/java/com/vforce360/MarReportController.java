package com.vforce360;

import com.vforce360.ports.MarReportPort;
import com.vforce360.model.AssessmentReport;
import com.vforce360.model.RenderedReportResponse;
import com.vforce360.service.ReportRenderingService;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for MAR Review Section.
 * 
 * GREEN PHASE UPDATE:
 * - Injects ReportRenderingService.
 * - Delegates content transformation to the service.
 * - Returns "text/html" content type in the response wrapper.
 */
@RestController
@RequestMapping("/api/projects/{projectId}/mar")
public class MarReportController {

    private final MarReportPort marReportPort;
    private final ReportRenderingService renderingService;

    // Constructor Injection
    public MarReportController(MarReportPort marReportPort, ReportRenderingService renderingService) {
        this.marReportPort = marReportPort;
        this.renderingService = renderingService;
    }

    @GetMapping("/review")
    public RenderedReportResponse getMarReview(@PathVariable String projectId) {
        // 1. Retrieve raw report data
        AssessmentReport report = marReportPort.getReport(projectId);
        
        if (report == null) {
            return new RenderedReportResponse("Report not found", "text/plain");
        }

        // 2. Transform raw content to HTML
        // Even if the DB contains Markdown, the service converts it to HTML.
        String htmlContent = renderingService.renderReportToHtml(report);

        // 3. Return formatted response
        return new RenderedReportResponse(htmlContent, "text/html");
    }
}
