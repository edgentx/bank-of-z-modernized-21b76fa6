package com.vforce360;

import com.vforce360.model.ModernizationAssessmentReport;
import com.vforce360.ports.ModernizationAssessmentPort;
import com.vforce360.ports.ReportRendererPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service layer for handling Modernization Assessment Report Review.
 * 
 * Implements the business logic required to pass the TDD tests.
 * TDD Green Phase: Connects the Data Port to the Renderer Port.
 */
@Service
public class MarReviewService {

    private final ModernizationAssessmentPort assessmentPort;
    private final ReportRendererPort rendererPort;

    @Autowired
    public MarReviewService(ModernizationAssessmentPort assessmentPort, ReportRendererPort rendererPort) {
        this.assessmentPort = assessmentPort;
        this.rendererPort = rendererPort;
    }

    /**
     * Retrieves the report as formatted Markdown.
     * Fixes S-1: Delegates to renderer instead of returning raw JSON.
     */
    public String getMarForReview(String projectId) {
        ModernizationAssessmentReport report = assessmentPort.findByProjectId(projectId);
        return rendererPort.toMarkdown(report);
    }

    /**
     * Retrieves the report as formatted HTML.
     */
    public String getMarHtml(String projectId) {
        ModernizationAssessmentReport report = assessmentPort.findByProjectId(projectId);
        return rendererPort.toHtml(report);
    }
}
