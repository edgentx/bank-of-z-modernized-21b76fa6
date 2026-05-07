package com.vforce360.service;

import com.vforce360.model.ModernizationAssessmentReport;
import com.vforce360.ports.MarRepositoryPort;
import com.vforce360.ports.ReportRendererPort;
import org.springframework.stereotype.Service;

/**
 * Service handling the logic for Modernization Assessment Reports.
 * GREEN PHASE: Contains the correct implementation to pass tests.
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
     * Implementation: Fetches raw markdown and delegates to the renderer port.
     * 
     * @param projectId The ID of the project.
     * @return The HTML representation of the report.
     * @throws RuntimeException if the report is not found.
     */
    public String getMarHtml(String projectId) {
        // 1. Fetch Report
        ModernizationAssessmentReport report = marRepository.findByProjectId(projectId);
        
        if (report == null) {
            throw new RuntimeException("MAR not found for project: " + projectId);
        }

        // 2. Render Markdown to HTML
        // This fixes the defect where raw content was displayed.
        return reportRenderer.renderMarkdownToHtml(report.getRawMarkdown());
    }
}