package com.vforce360.adapters;

import com.vforce360.models.MarReport;
import com.vforce360.ports.MarReportPort;
import com.vforce360.repository.MarReportRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class JpaMarReportAdapter implements MarReportPort {

    private final MarReportRepository repository;
    private final MarkdownConverter markdownConverter;

    public JpaMarReportAdapter(MarReportRepository repository, MarkdownConverter markdownConverter) {
        this.repository = repository;
        this.markdownConverter = markdownConverter;
    }

    @Override
    public MarReport findById(UUID id) {
        // Fetch from DB (assuming raw storage or conversion needed)
        MarReport report = repository.findById(id).orElseThrow(() -> new RuntimeException("MAR not found"));

        // Implement S-1 Fix: Convert raw content (if stored as markdown) to HTML before returning
        // If the DB stores raw JSON string in the content field, this is where we parse it.
        // Assuming the DB now stores the Markdown text, we render it to HTML.
        if (report.getRenderedContent() != null && !report.getRenderedContent().isEmpty()) {
            String html = markdownConverter.convertToHtml(report.getRenderedContent());
            report.setRenderedContent(html);
        }

        return report;
    }
}
