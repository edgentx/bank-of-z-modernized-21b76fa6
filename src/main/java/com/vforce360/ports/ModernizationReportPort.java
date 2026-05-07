package com.vforce360.ports;

import com.vforce360.domain.ModernizationAssessmentReport;

/**
 * Port interface for fetching Modernization Assessment Reports.
 * This allows us to mock the data source in tests without relying on a real MongoDB instance.
 */
public interface ModernizationReportPort {
    /**
     * Retrieves the report for a specific project ID.
     *
     * @param projectId The unique identifier of the brownfield project.
     * @return The report object containing structured data.
     */
    ModernizationAssessmentReport getReport(String projectId);
}