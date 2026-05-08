package com.example.e2e;

import com.example.domain.shared.DomainEvent;
import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.domain.vforce360.model.VForce360Aggregate;
import com.example.mocks.MockVForce360NotificationPort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Regression test for Story S-FB-1 / Defect VW-454.
 * Validates that the Slack body (via VForce360 port) contains the GitHub issue link.
 */
public class VW454ValidationE2ETest {

    private final MockVForce360NotificationPort mockPort = new MockVForce360NotificationPort();
    private static final String DEFECT_ID = "VW-454";
    private static final String EXPECTED_GITHUB_URL = "https://github.com/example/bank-of-z/issues/454";

    @AfterEach
    void tearDown() {
        mockPort.reset();
    }

    @Test
    @DisplayName("S-FB-1: Verify Slack body contains GitHub URL when defect is reported")
    void testSlackBodyContainsGitHubUrl() {
        // Arrange
        var aggregate = new VForce360Aggregate("test-agg-1", mockPort);
        var cmd = new ReportDefectCmd(
            DEFECT_ID,
            "Validating VW-454 — GitHub URL in Slack body",
            "LOW",
            EXPECTED_GITHUB_URL
        );

        // Act
        List<DomainEvent> events = aggregate.execute(cmd);

        // Assert
        // 1. Verify Aggregate emitted correct event
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isInstanceOf(DefectReportedEvent.class);

        // 2. Verify the Mock Port (representing Slack) received the payload
        // This simulates checking the "Slack body" or API payload
        var sentPayload = mockPort.getReport(DEFECT_ID);
        assertThat(sentPayload).isNotNull();

        // 3. CRITICAL ASSERTION: Verify GitHub URL is present
        // This is the core fix for VW-454.
        String actualUrl = sentPayload.get("githubUrl");
        assertThat(actualUrl)
            .as("Slack body must include GitHub issue URL")
            .isNotBlank()
            .isEqualTo(EXPECTED_GITHUB_URL);

        // Verify we didn't accidentally drop other fields
        assertThat(sentPayload.get("severity")).isEqualTo("LOW");
        assertThat(sentPayload.get("title")).contains("Validating");
    }

    @Test
    @DisplayName("S-FB-1: Regression check for missing GitHub URL")
    void testRegression_MissingGitHubUrlShouldFailOrBeEmpty() {
        // This test documents the defect state if the implementation was broken.
        // We pass a valid command with a URL, if the implementation fails to pass it to the port,
        // the assertion above fails.
        
        // Setup specific scenario: URL is present in command
        var aggregate = new VForce360Aggregate("test-agg-2", mockPort);
        var cmd = new ReportDefectCmd(
            DEFECT_ID, "Defect with missing link", "LOW", "https://github.com/ticket/1"
        );

        aggregate.execute(cmd);

        // Check that the link actually propagated to the "Slack body" (Mock Port)
        String sentUrl = mockPort.getReport(DEFECT_ID).get("githubUrl");
        
        // If this assertion passes, the defect is fixed.
        assertThat(sentUrl).isEqualTo("https://github.com/ticket/1");
    }

    @Test
    @DisplayName("S-FB-1: Handling of null GitHub URL")
    void testNullGitHubUrlHandling() {
        var aggregate = new VForce360Aggregate("test-agg-3", mockPort);
        
        // If null URLs are possible, we must ensure we don't NPE or send "null" string
        // Depending on domain rules, this might throw exception or send empty string.
        // Assuming Command Record allows nulls, but we expect validation.
        var cmd = new ReportDefectCmd(DEFECT_ID, "Defect No Link", "LOW", null);

        // Expectation: System handles gracefully (either throws or sends safe default)
        // For this test, we check that we didn't crash, but we sent something.
        try {
            aggregate.execute(cmd);
            var payload = mockPort.getReport(DEFECT_ID);
            // If it didn't throw, did it send the null?
            assertThat(payload.get("githubUrl")).isNull();
        } catch (IllegalArgumentException e) {
            // This is also acceptable validation behavior
            assertThat(e).hasMessageContaining("githubUrl");
        }
    }
}
