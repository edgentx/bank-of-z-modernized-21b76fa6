package com.example.domain.ports;

import java.util.concurrent.CompletableFuture;

/**
 * Port for persisting validation results (e.g., GitHub URLs).
 * Abstracts the storage mechanism (MongoDB, DB2, etc.) from the domain.
 */
public interface ValidationRepository {
    /**
     * Associates a defect ID with a GitHub URL.
     */
    CompletableFuture<Void> saveMapping(String defectId, String githubUrl);

    /**
     * Retrieves the GitHub URL for a given defect ID.
     */
    CompletableFuture<String> findGithubUrl(String defectId);
}