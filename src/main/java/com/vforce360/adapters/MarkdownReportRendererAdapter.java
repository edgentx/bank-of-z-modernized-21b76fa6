package com.vforce360.adapters;

import com.vforce360.model.ModernizationAssessmentReport;
import com.vforce360.ports.ReportRendererPort;
import com.vforce360.utils.MarkdownConverter;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Real Adapter for the ReportRendererPort.
 * Converts raw report data into formatted Markdown and HTML.
 * 
 * Pattern: Adapter Pattern.
 * Adapts the internal Map-based data structure into a readable String format.
 */
@Component
public class MarkdownReportRendererAdapter implements ReportRendererPort {

    @Override
    public String toMarkdown(ModernizationAssessmentReport report) {
        if (report == null) return "";

        StringBuilder md = new StringBuilder();
        md.append("# ").append(escapeMd(report.getTitle())).append("\n\n");
        md.append("**Project ID:** ").append(report.getProjectId()).append("\n\n");
        md.append("**Status:** ").append(report.getStatus()).append("\n\n");
        md.append("---\n\n");

        Map<String, Object> content = report.getContent();
        if (content != null) {
            md.append("## Assessment Details\n\n");
            content.forEach((key, value) -> {
                md.append("**").append(formatKey(key)).append("**: ");
                md.append(formatValue(value)).append("\n\n");
            });
        }

        return md.toString();
    }

    @Override
    public String toHtml(ModernizationAssessmentReport report) {
        // Convert to Markdown first, then use the utility to convert MD -> HTML
        String markdown = toMarkdown(report);
        return MarkdownConverter.toHtml(markdown);
    }

    private String formatKey(String key) {
        // Convert camelCase or snake_case to Title Case for display
        return key.substring(0, 1).toUpperCase() + key.substring(1).replaceAll("_", " ");
    }

    private String formatValue(Object value) {
        if (value == null) return "_N/A_";
        if (value instanceof Map) {
            // Simple handling for nested maps in Markdown
            return "\n" + mapToString((Map<?, ?>) value);
        }
        return value.toString();
    }

    private String mapToString(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            sb.append("  - **").append(entry.getKey()).append("**: ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }

    private String escapeMd(String text) {
        if (text == null) return "";
        // Basic sanitization to prevent MD injection in titles
        return text.replace("#", "\\#");
    }
}
