package com.vforce360;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vforce360.ports.MarPort;
import org.springframework.stereotype.Service;

/**
 * Service responsible for processing and formatting MAR data.
 * Implements the Green Phase logic to transform JSON into HTML.
 */
@Service
public class MarService {

    private final MarPort marPort;
    private final ObjectMapper objectMapper;

    public MarService(MarPort marPort) {
        this.marPort = marPort;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Retrieves the formatted MAR report for the CEO to review.
     *
     * @param projectId The unique identifier of the project.
     * @return HTML formatted string ready for display.
     */
    public String getFormattedReport(String projectId) {
        String rawContent = marPort.getMarContent(projectId);
        
        try {
            // Parse the raw JSON into a generic tree
            JsonNode rootNode = objectMapper.readTree(rawContent);
            
            // Build the HTML output
            StringBuilder html = new StringBuilder();
            html.append("<html><body>");
            html.append("<h1>Modernization Assessment Report</h1>");
            
            // Render Summary
            if (rootNode.has("summary")) {
                html.append("<h2>Summary</h2>");
                html.append("<p>").append(rootNode.get("summary").asText()).append("</p>");
            }
            
            // Render Recommendations as a bulleted list
            if (rootNode.has("recommendations")) {
                html.append("<h2>Recommendations</h2>");
                html.append("<ul>");
                for (JsonNode rec : rootNode.get("recommendations")) {
                    html.append("<li>").append(rec.asText()).append("</li>");
                }
                html.append("</ul>");
            }
            
            html.append("</body></html>");
            return html.toString();
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse MAR content", e);
        }
    }
}
