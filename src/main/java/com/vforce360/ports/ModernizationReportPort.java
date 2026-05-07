package com.vforce360.ports;

import com.vforce360.mar.model.ModernizationAssessmentReport;

import java.util.Optional;

/**
 * Repository Port for retrieving MAR data.
 */
public interface ModernizationReportPort {

    /**
     * Finds a report by project ID.
     *
     * @param projectId The UUID of the project.
     * @return The report if found.
     */
    Optional<ModernizationAssessmentReport> findByProjectId(String projectId);
}