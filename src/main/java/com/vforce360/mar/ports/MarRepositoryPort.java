package com.vforce360.mar.ports;

import com.vforce360.mar.model.MarDocument;

/**
 * Port interface for accessing Modernization Assessment Report data.
 * This acts as the boundary between the application logic and the data store.
 */
public interface MarRepositoryPort {

    /**
     * Retrieves the MAR document for a specific project ID.
     *
     * @param projectId The unique identifier of the project (e.g., UUID).
     * @return The MarDocument containing the raw content.
     * @throws IllegalArgumentException if the project ID is invalid.
     */
    MarDocument findByProjectId(String projectId);
}