package com.vforce360.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vforce360.ports.IModernizationReportRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReportService {

    private final IModernizationReportRepository repository;
    private final ObjectMapper objectMapper;

    // Constructor for dependency injection (required for tests)
    public ReportService(IModernizationReportRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    /**
     * Retrieves the report content for a given project and formats it for display.
     * Intended to convert raw JSON into readable Markdown.
     *
     * @param projectId The ID of the project.
     * @return Formatted string (Markdown).
     */
    public String getFormattedReport(String projectId) {
        Optional<String> rawContentOpt = repository.findRawContentByProjectId(projectId);

        if (rawContentOpt.isEmpty()) {
            throw new IllegalArgumentException("No report found for project: " + projectId);
        }

        String rawJson = rawContentOpt.get();
        
        // Placeholder implementation for Red Phase
        // This method currently does nothing or returns raw data, causing tests to fail.
        return rawJson; 
    }
}
