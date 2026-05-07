package com.vforce360;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.vforce360.model.MarReport;
import com.vforce360.ports.MarReportPort;

/**
 * Controller for viewing the Modernization Assessment Report.
 * Renders the report as HTML.
 */
@Controller
public class MarReportController {

    private final MarReportPort marReportPort;

    public MarReportController(MarReportPort marReportPort) {
        this.marReportPort = marReportPort;
    }

    @GetMapping("/projects/{projectId}/mar/review")
    public ResponseEntity<String> viewMarReport(@PathVariable String projectId) {
        // Fetch the report (raw JSON structure)
        MarReport report = marReportPort.findByProjectId(projectId);
        
        if (report == null) {
            return ResponseEntity.notFound().build();
        }

        // TODO: Render the 'summary' field content to HTML instead of returning raw JSON
        // This is the defect location.
        return ResponseEntity.ok(report.getRawContent()); 
    }
}