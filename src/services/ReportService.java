package com.vforce360.app.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vforce360.app.models.ReportDisplayModel;
import com.vforce360.shared.ports.MarkdownRendererPort;
import com.vforce360.shared.ports.ModernizationReportPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service layer handling the logic for fetching and preparing the MAR for display.
 * 
 * FIX: Modified to detect JSON payload, extract the 'content' field,
 * and pass only the markdown string to the renderer.
 */
@Service
public class ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);
    private final ModernizationReportPort reportPort;
    private final MarkdownRendererPort rendererPort;
    private final ObjectMapper objectMapper;

    public ReportService(ModernizationReportPort reportPort, MarkdownRendererPort rendererPort) {
        this.reportPort = reportPort;
        this.rendererPort = rendererPort;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Prepares the report for the UI view.
     */
    public Optional<ReportDisplayModel> getReportForDisplay(String projectId) {
        // 1. Fetch raw data
        Optional<String> rawContent = reportPort.findRawContentByProjectId(projectId);

        if (rawContent.isEmpty()) {
            return Optional.empty();
        }

        String content = rawContent.get();

        // 2. Transform data (The Fix)
        // Logic: Check if content is JSON. If yes, extract "content" field.
        // If no (or parsing fails), treat as raw markdown.
        String markdownToRender = extractMarkdown(content);

        // 3. Render HTML
        String htmlContent = rendererPort.renderToHtml(markdownToRender);

        return Optional.of(new ReportDisplayModel(projectId, htmlContent));
    }

    /**
     * Helper method to parse JSON and extract the 'content' field containing markdown.
     * If parsing fails, it assumes the input is raw markdown.
     *
     * @param rawInput The string from the database.
     * @return The clean markdown string.
     */
    private String extractMarkdown(String rawInput) {
        try {
            JsonNode rootNode = objectMapper.readTree(rawInput);
            // Check for "content" field. If missing, we might have a partial JSON, 
            // but based on defect description, we expect "content".
            if (rootNode.has("content")) {
                return rootNode.get("content").asText();
            } else {
                // It's JSON, but no content field? Fallback to raw to avoid breaking existing logic.
                log.warn("JSON structure found, but 'content' field missing. Returning raw input.");
                return rawInput;
            }
        } catch (JsonProcessingException e) {
            // Not a JSON object. Assume it is legacy raw markdown.
            return rawInput;
        }
    }
}
