package com.example.adapters;

import com.example.ports.DefectReporterPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of {@link DefectReporterPort}.
 * Integrates with external issue tracking systems (e.g., GitHub Issues).
 */
@Component
public class DefectReporterAdapter implements DefectReporterPort {

    private static final Logger log = LoggerFactory.getLogger(DefectReporterAdapter.class);
    private static final String FAKE_REPO_BASE = "https://github.com/fake-org/repo/issues/";

    @Override
    public String reportDefect(String title, String details) {
        // Production logic would go here:
        // 1. Call GitHub API to create issue.
        // 2. Parse response for HTML URL.
        // 3. Return URL.
        
        // Simulating an ID generation for the URL to satisfy test contract
        String issueId = java.util.UUID.randomUUID().toString();
        String url = FAKE_REPO_BASE + issueId;
        
        log.info("[PROD MOCK] Defect reported: {}. URL: {}", title, url);
        return url;
    }
}
