package com.vforce360;

import com.vforce360.ports.MarReportPort;
import com.vforce360.model.AssessmentReport;
import com.vforce360.model.RenderedReportResponse;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for MAR Review Section.
 * Placeholder implementation to satisfy compilation during Red Phase.
 * This class will be modified to fix the bug in Green Phase.
 */
@RestController
@RequestMapping("/api/projects/{projectId}/mar")
public class MarReportController {

    private final MarReportPort marReportPort;

    public MarReportController(MarReportPort marReportPort) {
        this.marReportPort = marReportPort;
    }

    @GetMapping("/review")
    public RenderedReportResponse getMarReview(@PathVariable String projectId) {
        // DEFECTIVE CODE (Red Phase):
        // Currently returns raw content wrapped in JSON.
        // This causes the test to fail, satisfying the TDD Red Phase requirement.
        AssessmentReport report = marReportPort.getReport(projectId);
        
        if (report == null) {
            return new RenderedReportResponse("Not Found", "text/plain");
        }

        // BUG: Returning raw content directly without parsing/rendering to HTML.
        // The test explicitly checks that the content starts with '<' (HTML) 
        // and does not start with '{' (JSON).
        return new RenderedReportResponse(report.getRawContent(), "application/json");
    }
}
