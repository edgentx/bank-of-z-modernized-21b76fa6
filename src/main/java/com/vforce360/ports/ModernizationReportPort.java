package com.vforce360.ports;

import com.vforce360.mar.models.ModernizationAssessmentReport;

/**
 * Port interface for retrieving Modernization Assessment Report data.
 * This abstracts the storage mechanism (MongoDB, DB2, etc.) from the application logic.
 */
public interface ModernizationReportPort {

    /**
     * Retrieves the raw report data for a specific project.
     * @param projectId The unique identifier of the project.
     * @return The report object containing raw data/metadata.
     */
    ModernizationAssessmentReport getReport(String projectId);
}
