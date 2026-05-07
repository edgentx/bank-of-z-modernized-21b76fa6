package com.vforce360.ports;

import com.vforce360.model.ModernizationAssessmentReport;

/**
 * Port interface for retrieving Modernization Assessment Report data.
 */
public interface MarRepositoryPort {

    /**
     * Finds the MAR content for a specific project.
     * @param projectId The unique identifier of the project.
     * @return The MAR entity.
     */
    ModernizationAssessmentReport findByProjectId(String projectId);
}