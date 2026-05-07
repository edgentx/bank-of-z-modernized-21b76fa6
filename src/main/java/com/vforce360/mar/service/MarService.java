package com.vforce360.mar.service;

import com.vforce360.mar.ports.MarRepositoryPort;
import org.springframework.stereotype.Service;

/**
 * Service layer for Modernization Assessment Reports.
 * This class is intentionally skeletal (Red Phase).
 * It needs to be implemented to make the tests pass.
 */
@Service
public class MarService {

    private final MarRepositoryPort repository;

    public MarService(MarRepositoryPort repository) {
        this.repository = repository;
    }

    /**
     * Retrieves the MAR content for a project and returns it as a formatted HTML string.
     * 
     * @param projectId The ID of the project.
     * @return HTML formatted string.
     */
    public String getRenderedReport(String projectId) {
        // TODO: Implement Markdown -> HTML conversion logic here.
        // Currently throws error to simulate missing implementation.
        throw new UnsupportedOperationException("getRenderedReport is not implemented yet.");
    }
}