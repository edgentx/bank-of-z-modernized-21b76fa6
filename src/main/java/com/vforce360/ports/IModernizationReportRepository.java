package com.vforce360.ports;

import java.util.Optional;

/**
 * Port interface for retrieving Modernization Assessment Report content.
 * Abstracts the storage mechanism (MongoDB/DB2) from the service layer.
 */
public interface IModernizationReportRepository {

    /**
     * Retrieves the raw content associated with a project ID.
     * This content is expected to be a JSON string.
     *
     * @param projectId The unique identifier of the project.
     * @return Optional containing the raw JSON string, or empty if not found.
     */
    Optional<String> findRawContentByProjectId(String projectId);
}
