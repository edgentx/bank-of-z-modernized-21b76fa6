package com.vforce360.service;

import com.vforce360.model.ModernizationAssessmentReport;
import com.vforce360.ports.MarRepositoryPort;
import com.vforce360.ports.ReportRendererPort;
import org.springframework.stereotype.Service;

/**
 * Service handling the logic for Modernization Assessment Reports.
 * This is the skeleton implementation (RED PHASE). 
 * It compiles but the logic is incomplete/incorrect, causing tests to fail.
 */
@Service
public class MarService {

    private final MarRepositoryPort marRepository;
    private final ReportRendererPort reportRenderer;

    public MarService(MarRepositoryPort marRepository, ReportRendererPort reportRenderer) {
        this.marRepository = marRepository;
        this.reportRenderer = reportRenderer;
    }

    /**
     * Retrieves the MAR content as formatted HTML.
     * 
     * @param projectId The ID of the project.
     * @return The HTML representation of the report.
     * @throws RuntimeException if the report is not found.
     */
    public String getMarHtml(String projectId) {
        // INTENTIONAL BUG / RED PHASE STUB
        // This method currently returns a raw JSON string instead of rendered HTML.
        // The tests expect HTML, but this returns the defect behavior.
        
        // 1. Fetch Report
        ModernizationAssessmentReport report = marRepository.findByProjectId(projectId);
        
        if (report == null) {
            throw new RuntimeException("MAR not found for project: " + projectId);
        }

        // DEFECT SIMULATION: Returning raw markdown/content without rendering
        // The test `getMarHtml_shouldReturnRenderedContent_whenReportExists` expects HTML.
        return report.getRawMarkdown();
        
        // CORRECT IMPLEMENTATION (to be done in Green phase):
        // return reportRenderer.renderMarkdownToHtml(report.getRawMarkdown());
    }
}