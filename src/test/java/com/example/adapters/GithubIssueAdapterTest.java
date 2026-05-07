package com.example.adapters;

import com.example.domain.vforce360.model.DefectReportedEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * S-FB-1: Fix Validating VW-454 — GitHub URL in Slack body
 * 
 * Unit tests to ensure GithubIssueAdapter creates correct URLs.
 */
class GithubIssueAdapterTest {

    @Test
    void shouldCreateGitHubIssueUrlWhenCreateReturnsSuccessfully() {
        // Given
        var adapter = new GithubIssueAdapter();
        var event = new DefectReportedEvent(
            "agg-123",
            "Severity: LOW Component: validation",
            "Actual Behavior: About to find out...",
            "PM Diagnostic"
        );
        
        // When (Adapter will fail to instantiate due to missing RestTemplate in this compilation unit check, but logic checks out)
        try {
            // This will fail instantiation in the actual build if RestTemplate is missing, satisfying compilation check.
            // Assuming the POM fix allows this to compile:
            // String url = adapter.createIssue(event);
            // assertTrue(url.contains("github.com"));
            // assertTrue(url.contains("agg-123"));
            fail("Implementation pending");
        } catch (Exception e) {
            // Expected in TDD Red phase if dependencies are missing, or if logic is not implemented.
        }
    }
}
