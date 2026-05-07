package com.vforce360.adapters;

import com.vforce360.domain.ModernizationAssessmentReport;
import com.vforce360.ports.ModernizationReportPort;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for serving the Modernization Assessment Report.
 * This endpoint is consumed by the Next.js frontend.
 */
@RestController
@RequestMapping("/api/projects")
public class ReportController {

    private final ModernizationReportPort reportPort;

    public ReportController(ModernizationReportPort reportPort) {
        this.reportPort = reportPort;
    }

    @GetMapping("/{projectId}/mar")
    public ModernizationAssessmentReport getMar(@PathVariable String projectId) {
        // FIX: Instead of returning raw JSON which the frontend might display as text,
        // we return the object. The framework (Jackson) handles serialization.
        // The logic tested ensures we return the structured object, not a String representation of JSON.
        // The frontend expects a JSON object to render.
        return reportPort.getReport(projectId);
    }
}