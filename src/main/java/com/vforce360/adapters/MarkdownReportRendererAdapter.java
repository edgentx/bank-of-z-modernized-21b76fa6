package com.vforce360.adapters;

import com.vforce360.model.ModernizationAssessmentReport;
import com.vforce360.ports.ReportRendererPort;
import com.vforce360.utils.MarkdownConverter;
import org.springframework.stereotype.Component;

/**
 * Adapter implementation for rendering MAR reports.
 * FIXED: Corrected method override and resolved 'cannot find symbol' for getters.
 */
@Component
public class MarkdownReportRendererAdapter implements ReportRendererPort {

    @Override
    public String renderMarkdownToHtml(String markdownContent) {
        return MarkdownConverter.toHtml(markdownContent);
    }

    /**
     * Orchestrates the rendering of the full report entity to HTML.
     */
    public String renderReportToHtml(ModernizationAssessmentReport report) {
        if (report == null) return "<div>No Report Found</div>";

        StringBuilder sb = new StringBuilder();
        sb.append("<div class='mar-report'>");
        // 1. Render Title (H1)
        sb.append("<h1>").append(escapeHtml(report.getTitle())).append("</h1>");
        // 2. Render Metadata
        sb.append("<p class='meta'><strong>Project ID:</strong> ").append(escapeHtml(report.getProjectId())).append("</p>");
        // 3. Render Body (Markdown -> HTML)
        String htmlBody = renderMarkdownToHtml(report.getRawMarkdown());
        sb.append("<div class='content'>").append(htmlBody).append("</div>");
        sb.append("</div>");
        return sb.toString();
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}