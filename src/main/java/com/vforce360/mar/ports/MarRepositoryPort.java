package com.vforce360.mar.ports;

import java.util.Optional;
import java.util.UUID;

/**
 * Port interface for retrieving Modernization Assessment Report data.
 * Implementations will abstract the underlying database (e.g., MongoDB via GridFS, DB2).
 */
public interface MarRepositoryPort {
    
    /**
     * Finds the raw MAR content for a specific project.
     * @param projectId The UUID of the project.
     * @return Optional containing the raw string data (likely JSON or Markdown), or empty if not found.
     */
    Optional<String> findByProjectId(UUID projectId);
}
