package com.vforce360.ports;

import com.vforce360.model.AssessmentReport;

/**
 * Port interface for retrieving Modernization Assessment Report data.
 * Implemented by Adapters (MongoDB, HTTP, etc.).
 */
public interface MarReportPort {

    /**
     * Retrieves the raw report data for a specific project context.
     * 
     * @param projectId The unique identifier of the project.
     * @return AssessmentReport containing the report content.
     */
    AssessmentReport getReport(String projectId);
}
