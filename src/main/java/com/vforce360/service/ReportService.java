package com.vforce360.service;

import org.springframework.stereotype.Service;

/**
 * Service for handling Modernization Assessment Report logic.
 * Placeholder for Red Phase.
 */
@Service
public class ReportService {

    /**
     * Retrieves the formatted report content for a specific project.
     * This method is currently missing the implementation to convert JSON to Markdown/HTML.
     *
     * @param projectId The UUID of the project.
     * @return The formatted string (Markdown or HTML).
     */
    public String getFormattedReport(String projectId) {
        // RED PHASE: Intentionally returning raw JSON (or incorrect format) to fail the test.
        // The test `testServiceTransformsRawJsonToMarkdown` expects Markdown, but gets this.
        return "{\"projectId\": \"" + projectId + "\", \"status\": \"RAW_JSON\"}";
    }
}
