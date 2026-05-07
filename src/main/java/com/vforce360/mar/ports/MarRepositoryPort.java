package com.vforce360.mar.ports;

import com.vforce360.mar.domain.ModernizationAssessmentReport;
import java.util.Optional;
import java.util.UUID;

/**
 * Port interface for retrieving Modernization Assessment Reports.
 * Abstracts the storage mechanism (MongoDB/DB2) from the application logic.
 */
public interface MarRepositoryPort {

    /**
     * Finds a MAR by its associated project ID.
     * @param projectId The UUID of the project.
     * @return Optional containing the MAR if found.
     */
    Optional<ModernizationAssessmentReport> findByProjectId(UUID projectId);
}
