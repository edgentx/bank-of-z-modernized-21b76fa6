package com.vforce360.ports;

import com.vforce360.model.ModernizationAssessmentReport;

/**
 * Port interface responsible for converting a raw Report into a viewable format (Markdown/HTML).
 * The defect occurred because the system skipped this rendering step and returned the raw object.
 */
public interface ReportRendererPort {

    /**
     * Renders the report content into a Markdown formatted string.
     *
     * @param report The report to render.
     * @return A string containing Markdown.
     */
    String toMarkdown(ModernizationAssessmentReport report);

    /**
     * Renders the report content into an HTML formatted string.
     *
     * @param report The report to render.
     * @return A string containing HTML.
     */
    String toHtml(ModernizationAssessmentReport report);
}
