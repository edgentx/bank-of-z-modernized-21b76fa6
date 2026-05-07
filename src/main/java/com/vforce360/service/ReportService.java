package com.vforce360.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vforce360.ports.IModernizationReportRepository;
import com.vforce360.ports.ReportData;
import com.vforce360.ports.ReportIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for handling Modernization Assessment Report logic.
 * Implements the Green Phase logic to transform raw JSON into formatted Markdown.
 */
@Service
public class ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);
    private final IModernizationReportRepository repository;
    private final ObjectMapper objectMapper;

    public ReportService(IModernizationReportRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    /**
     * Retrieves the formatted report content for a specific project.
     * This implementation fetches raw data and transforms it to Markdown.
     *
     * @param projectId The UUID of the project.
     * @return The formatted string (Markdown).
     */
    public String getFormattedReport(String projectId) {
        log.debug("Generating formatted report for project ID: {}", projectId);

        // 1. Retrieve raw data from the repository
        ReportIdentifier id = ReportIdentifier.of(projectId);
        Optional<ReportData> dataOpt = repository.findById(id);

        // 2. Handle missing data gracefully
        if (dataOpt.isEmpty()) {
            return "# Report Not Found\n\nNo report data exists for Project ID: " + projectId;
        }

        ReportData data = dataOpt.get();
        String rawJson = data.getContentJson();

        // 3. Transform JSON to Markdown
        try {
            return convertJsonToMarkdown(rawJson, projectId);
        } catch (Exception e) {
            log.error("Failed to parse report JSON for project {}", projectId, e);
            return "# Error Rendering Report\n\nThe source data is malformed.";
        }
    }

    /**
     * Converts a JSON string into a Markdown representation.
     * This simulates the parsing of the structured report data into a readable document.
     */
    private String convertJsonToMarkdown(String jsonContent, String projectId) throws Exception {
        StringBuilder md = new StringBuilder();

        // Parse JSON
        JsonNode root = objectMapper.readTree(jsonContent);

        md.append("# Modernization Assessment Report\n\n");
        md.append("**Project ID:** ").append(projectId).append("\n\n");
        md.append("---\n\n");

        // Extract Status
        JsonNode statusNode = root.get("status");
        if (statusNode != null) {
            md.append("## Status\n\n");
            md.append("* ").append(statusNode.asText()).append("\n\n");
        }

        // Extract Summary
        JsonNode summaryNode = root.get("summary");
        if (summaryNode != null) {
            md.append("## Executive Summary\n\n");
            md.append(summaryNode.asText()).append("\n\n");
        }

        md.append("## Detailed Analysis\n\n");
        md.append("(Full details would be parsed here...)\n\n");

        return md.toString();
    }
}
