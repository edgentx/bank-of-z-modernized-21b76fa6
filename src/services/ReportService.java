package com.vforce360.app.services;

import com.vforce360.app.models.ReportDisplayModel;
import com.vforce360.shared.ports.MarkdownRendererPort;
import com.vforce360.shared.ports.ModernizationReportPort;
import org.springframework.stereotype.Service;
import java.util.Optional;

/**
 * Service layer handling the logic for fetching and preparing the MAR for display.
 * This class will need modification to fix the defect (parse JSON, extract Markdown, render).
 */
@Service
public class ReportService {

    private final ModernizationReportPort reportPort;
    private final MarkdownRendererPort rendererPort;

    public ReportService(ModernizationReportPort reportPort, MarkdownRendererPort rendererPort) {
        this.reportPort = reportPort;
        this.rendererPort = rendererPort;
    }

    /**
     * Prepares the report for the UI view.
     * 
     * DEFECT CONTEXT: Currently passes raw JSON to UI.
     * EXPECTED: Passes formatted HTML.
     */
    public Optional<ReportDisplayModel> getReportForDisplay(String projectId) {
        // 1. Fetch raw data
        Optional<String> rawContent = reportPort.findRawContentByProjectId(projectId);

        if (rawContent.isEmpty()) {
            return Optional.empty();
        }

        String content = rawContent.get();

        // 2. Transform data
        // TODO: Implement logic to detect JSON, parse it, and extract the 'markdown' field.
        // Current (Defective) Behavior: Just renders whatever is there directly.
        // If the DB returns JSON, this will pass JSON to the UI (Bug).
        
        String htmlContent = rendererPort.renderToHtml(content);

        return Optional.of(new ReportDisplayModel(projectId, htmlContent));
    }
}
