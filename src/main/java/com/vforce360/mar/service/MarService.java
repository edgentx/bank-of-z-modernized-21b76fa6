package com.vforce360.mar.service;

import com.vforce360.mar.ports.MarRepositoryPort;
import com.vforce360.mar.model.MarDocument;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Service;

/**
 * Service layer for Modernization Assessment Reports.
 * Handles the transformation of raw Markdown content to HTML.
 */
@Service
public class MarService {

    private final MarRepositoryPort repository;
    private final Parser markdownParser;
    private final HtmlRenderer htmlRenderer;

    public MarService(MarRepositoryPort repository) {
        this.repository = repository;
        this.markdownParser = Parser.builder().build();
        this.htmlRenderer = HtmlRenderer.builder().build();
    }

    /**
     * Retrieves the MAR content for a project and returns it as a formatted HTML string.
     *
     * @param projectId The ID of the project.
     * @return HTML formatted string representing the report.
     */
    public String getRenderedReport(String projectId) {
        // 1. Retrieve the raw document from the repository port (interface)
        MarDocument doc = repository.findByProjectId(projectId);

        // 2. Extract the raw markdown content
        String rawMarkdown = doc.getContentMarkdown();

        // 3. Parse the Markdown to an AST
        // Note: Even if the content is technically JSON (as per the defect scenario),
        // we treat it as a text block. The renderer will escape HTML entities,
        // ensuring valid output (e.g. { becomes &123;).
        var document = markdownParser.parse(rawMarkdown);

        // 4. Render the AST as HTML
        return htmlRenderer.render(document);
    }
}