package com.example.e2e;

import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.repository.ValidationRepository;
import com.example.workflows.ReportDefectActivity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Regression test for S-FB-1 / VW-454.
 * Verifies that when a defect is reported via Temporal worker,
 * the resulting state (simulated by return value) includes the GitHub URL.
 */
@SpringBootTest
class VW454ValidationE2ETest {

    @Autowired
    private ReportDefectActivity activity;

    @SpyBean
    private ValidationRepository validationRepository;

    @Test
    void testSlackBodyContainsGitHubUrl() {
        String validationId = "VW-454";
        String summary = "Validating VW-454 — GitHub URL in Slack body";
        String description = "Defect reported by user via VForce360.";
        String severity = "LOW";
        String expectedUrl = "https://github.com/bank-of-z/issues/454";

        // Execution
        String resultUrl = activity.reportAndLinkDefect(
            validationId, 
            summary, 
            description, 
            severity, 
            expectedUrl
        );

        // Verify: The returned URL is not null and matches expectation
        assertNotNull(resultUrl, "GitHub URL should be returned in the body payload");
        assertTrue(resultUrl.contains(expectedUrl), "Slack body should include GitHub issue link");

        // Verify: Repository state was persisted correctly
        verify(validationRepository, times(2)).save(any(ValidationAggregate.class));
    }

    @Test
    void testReportDefectWithoutUrlDoesNotCrash() {
        String validationId = "VW-455";
        String resultUrl = activity.reportAndLinkDefect(validationId, "Sum", "Desc", "LOW", null);
        
        // Expected: URL is null because we didn't provide one
        // The fix ensures we don't crash if url is null, or if it's provided, we link it.
        // (Current implementation simply returns the URL if linked, or null if not)
    }
}
