package com.vforce360.ports;

import com.vforce360.model.ModernizationAssessmentReport;

/**
 * Port interface for retrieving Modernization Assessment Reports.
 * This abstraction allows us to mock the data source in tests without
 * hitting the real DB2 or MongoDB instances.
 */
public interface ModernizationAssessmentPort {

    /**
     * Retrieves the MAR for a specific project.
     *
     * @param projectId The unique identifier of the project.
     * @return The report object containing raw data.
     * @throws IllegalArgumentException if the project does not exist.
     */
    ModernizationAssessmentReport findByProjectId(String projectId);
}
