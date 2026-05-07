package com.vforce360.mar.controller;

import com.vforce360.mar.ports.MarRendererPort;
import com.vforce360.mar.ports.MarRepositoryPort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST Controller for Modernization Assessment Report (MAR).
 * Addresses defect S-1: Ensures MAR is rendered as HTML, not returned as raw JSON.
 */
@RestController
@RequestMapping("/api/mar")
public class MarController {

    private final MarRepositoryPort marRepository;
    private final MarRendererPort marRenderer;

    public MarController(MarRepositoryPort marRepository, MarRendererPort marRenderer) {
        this.marRepository = marRepository;
        this.marRenderer = marRenderer;
    }

    /**
     * Retrieves and renders the Modernization Assessment Report for a specific project.
     * 
     * @param projectId The UUID of the project.
     * @return ResponseEntity containing HTML content, or 404 if not found.
     */
    @GetMapping("/{projectId}")
    public ResponseEntity<String> getRenderedReport(@PathVariable UUID projectId) {
        // 1. Retrieve raw content (JSON string containing Markdown)
        return marRepository.findByProjectId(projectId)
            .map(rawContent -> {
                // 2. Transform raw content into HTML using the Renderer port
                String htmlContent = marRenderer.renderMarkdown(rawContent);
                
                // 3. Return as HTML (Fix for S-1)
                return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(htmlContent);
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
