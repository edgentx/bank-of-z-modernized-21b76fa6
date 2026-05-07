package com.vforce360.mar.service;

import com.vforce360.mar.model.ModernizationAssessmentReport;
import com.vforce360.mar.model.ReportDisplayDto;
import com.vforce360.ports.MarkdownRendererPort;
import com.vforce360.ports.ModernizationReportPort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MarService {

    private final ModernizationReportPort reportRepository;
    private final MarkdownRendererPort rendererAdapter;

    public MarService(ModernizationReportPort reportRepository, MarkdownRendererPort rendererAdapter) {
        this.reportRepository = reportRepository;
        this.rendererAdapter = rendererAdapter;
    }

    /**
     * Retrieves the report for the given project and ensures it is formatted.
     * Fixes the bug where raw JSON was returned.
     */
    public ReportDisplayDto getFormattedReport(String projectId) {
        Optional<ModernizationAssessmentReport> reportOpt = reportRepository.findByProjectId(projectId);

        if (reportOpt.isEmpty()) {
            return null;
        }

        ModernizationAssessmentReport report = reportOpt.get();

        // The Fix: Instead of returning raw JSON/Content, we use the adapter to render Markdown to HTML
        String renderedHtml = rendererAdapter.renderToHtml(report.getRawContent());

        return new ReportDisplayDto(renderedHtml);
    }
}