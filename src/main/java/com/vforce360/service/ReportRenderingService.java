package com.vforce360.service;

import com.vforce360.model.AssessmentReport;
import com.vforce360.ports.RenderingEnginePort;
import org.springframework.stereotype.Service;

/**
 * Service responsible for handling the business logic of report retrieval and rendering.
 * This effectively bridges the raw data from the persistence layer with the presentation layer.
 */
@Service
public class ReportRenderingService {

    private final RenderingEnginePort renderingEngine;

    // Constructor Injection (Spring Boot Convention)
    public ReportRenderingService(RenderingEnginePort renderingEngine) {
        this.renderingEngine = renderingEngine;
    }

    /**
     * Processes the raw report into a viewable format (HTML).
     * 
     * @param report The raw report from the database/adapter.
     * @return HTML string.
     */
    public String renderReportToHtml(AssessmentReport report) {
        if (report == null || report.getRawContent() == null) {
            return "";
        }

        // If the raw content is already HTML (or we want to treat it as such), return it.
        // For this specific defect fix, we assume we need to convert Markdown to HTML.
        return renderingEngine.convertMarkdownToHtml(report.getRawContent());
    }
}
