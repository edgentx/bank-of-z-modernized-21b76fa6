package com.vforce360.shared.ports;

import java.util.Optional;

/**
 * Port interface for retrieving Modernization Assessment Report (MAR) data.
 * This acts as the boundary between the application logic and the data source (DB2/Mongo).
 */
public interface ModernizationReportPort {

    /**
     * Retrieves the raw content payload for a specific project ID.
     * In the current defect state, this is expected to be a JSON string.
     *
     * @param projectId The unique identifier of the brownfield project.
     * @return Optional containing the raw report string, or empty if not found.
     */
    Optional<String> findRawContentByProjectId(String projectId);
}
