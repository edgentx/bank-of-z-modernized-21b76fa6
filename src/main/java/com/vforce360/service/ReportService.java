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

    /**
     * Constructor for dependency injection.
     *
     * @param repository The port for retrieving report data.
     * @param objectMapper Jackson ObjectMapper for JSON processing.
     */
    public ReportService(IModernizationReportRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    /**
     * Retrieves the report content for a given project and formats it as Markdown.
     *
     * @param projectId The ID of the project.
     * @return Formatted Markdown string.
     * @throws IllegalArgumentException if content is missing or cannot be processed.
     */
    public String getFormattedReport(String projectId) {
        Optional<String> rawContentOpt = repository.findRawContentByProjectId(projectId);

        if (rawContentOpt.isEmpty()) {
            throw new IllegalArgumentException("No report found for project: " + projectId);
        }

        String jsonContent = rawContentOpt.get();
        return convertJsonToFormattedMarkdown(jsonContent);
    }

    /**
     * Converts a JSON string into a Markdown formatted string.
     * This implementation handles Objects and Arrays, converting keys to headers and lists to bullets.
     *
     * @param jsonContent The raw JSON string.
     * @return A Markdown formatted string.
     */
    private String convertJsonToFormattedMarkdown(String jsonContent) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonContent);
            StringBuilder markdown = new StringBuilder();
            convertNodeToMarkdown(rootNode, markdown, 0);
            return markdown.toString();
        } catch (Exception e) {
            // If JSON parsing fails (malformed), wrap in code block as per requirements
            // Using standard string concatenation to avoid multiline literal syntax issues
            return "```\n" + jsonContent + "\n```";
        }
    }

    /**
     * Recursive helper to traverse the JSON node and build Markdown.
     *
     * @param node The current JSON node.
     * @param sb The StringBuilder to append to.
     * @param depth Current depth for header sizing.
     */
    private void convertNodeToMarkdown(JsonNode node, StringBuilder sb, int depth) {
        if (node.isObject()) {
            node.fields().forEachRemaining(entry -> {
                JsonNode child = entry.getValue();
                String key = entry.getKey();

                if (child.isObject() || child.isArray()) {
                    // Create a header for the key based on depth
                    sb.append("\n");
                    sb.append("#".repeat(Math.min(depth + 1, 6))) ; // Markdown headers max at 6
                    sb.append(" ").append(key).append("\n");
                    convertNodeToMarkdown(child, sb, depth + 1);
                } else {
                    // Leaf node (primitive value)
                    // Format as: **Key**: Value
                    sb.append("**" + key + "**: " + child.asText() + "\n\n");
                }
            });
        } else if (node.isArray()) {
            for (JsonNode item : node) {
                if (item.isObject() || item.isArray()) {
                    // Nested structure within array
                    convertNodeToMarkdown(item, sb, depth);
                } else {
                    // Simple list item
                    sb.append("- ").append(item.asText()).append("\n");
                }
            }
        }
    }
}
