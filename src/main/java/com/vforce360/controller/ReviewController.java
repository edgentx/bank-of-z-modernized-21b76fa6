package com.vforce360.controller;

import com.vforce360.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for the MAR Review section.
 */
@Controller
@RequestMapping("/api/projects")
public class ReviewController {

    @Autowired
    private ReportService reportService;

    /**
     * Endpoint to view the MAR review.
     * Currently returns raw JSON text instead of HTML, failing the S-1 test.
     */
    @GetMapping(value = "/{projectId}/mar/review", produces = MediaType.TEXT_HTML_VALUE)
    public String viewMarReview(@PathVariable String projectId) {
        // RED PHASE: Simply returning the raw JSON string from the service.
        // The browser will display this as text, failing the test check for HTML tags.
        return reportService.getFormattedReport(projectId);
    }
}
