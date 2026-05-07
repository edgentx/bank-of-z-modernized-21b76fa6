package com.vforce360.mar.service;

import com.vforce360.mar.domain.ModernizationAssessmentReport;
import com.vforce360.mar.ports.MarRepositoryPort;
import com.vforce360.mar.ports.MarkdownRendererPort;
import org.springframework.stereotype.Service;
import java.util.UUID;

/**
 * Service handling the business logic for Modernization Assessment Reports.
 * It coordinates fetching data (Port) and rendering it (Port).
 */
@Service
public class MarService {

    private final MarRepositoryPort marRepositoryPort;
    private final MarkdownRendererPort markdownRendererPort;

    public MarService(MarRepositoryPort marRepositoryPort, MarkdownRendererPort markdownRendererPort) {
        this.marRepositoryPort = marRepositoryPort;
        this.markdownRendererPort = markdownRendererPort;
    }

    /**
     * Retrieves the MAR content for a given project and returns it as rendered HTML.
     * 
     * @param projectId The UUID of the project.
     * @return String containing the HTML representation of the report.
     * @throws RuntimeException if the report is not found.
     */
    public String getMarReviewHtml(UUID projectId) {
        ModernizationAssessmentReport report = marRepositoryPort.findByProjectId(projectId)
                .orElseThrow(() -> new RuntimeException("MAR not found for project: " + projectId));

        // The raw content in the DB is stored as Markdown (as per the expected behavior).
        // We simply pass it to the renderer.
        return markdownRendererPort.renderToHtml(report.getRawContent());
    }
}