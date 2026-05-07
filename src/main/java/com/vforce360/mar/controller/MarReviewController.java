package com.vforce360.mar.controller;

import com.vforce360.mar.model.ReportDisplayDto;
import com.vforce360.mar.service.MarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mar")
public class MarReviewController {

    private final MarService marService;

    public MarReviewController(MarService marService) {
        this.marService = marService;
    }

    /**
     * Endpoint to view the MAR review section.
     * Corresponds to: "Navigate to a brownfield project with MAR generated"
     */
    @GetMapping("/project/{projectId}/review")
    public ResponseEntity<ReportDisplayDto> getReportReview(@PathVariable String projectId) {
        ReportDisplayDto report = marService.getFormattedReport(projectId);
        if (report == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(report);
    }
}