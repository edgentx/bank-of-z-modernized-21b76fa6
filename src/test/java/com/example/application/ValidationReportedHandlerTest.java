package com.example.application;

import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.model.ValidationReportedEvent;
import com.example.domain.validation.repository.ValidationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {ValidationReportedHandler.class})
class ValidationReportedHandlerTest {

    @Autowired
    private ValidationReportedHandler handler;

    @MockBean
    private GitHubService gitHubService;

    @MockBean
    private SlackService slackService; // We might wire this in later, but for now we just ensure GitHub is called.

    @MockBean
    private ValidationRepository repository;

    @Autowired
    private ApplicationEventPublisher publisher;

    /**
     * Acceptance Criteria: The validation no longer exhibits the reported behavior.
     * Regression test added to e2e/regression/ covering this scenario.
     * 
     * Test Scenario: When a defect is reported via Temporal/Temporal-worker exec, 
     * the resulting domain event should trigger the GitHub service, 
     * and the resulting URL should be verifiable (implicitly in Slack body).
     */
    @Test
    void shouldTriggerGitHubServiceWhenDefectReported() throws Exception {
        // Given
        String expectedUrl = "https://github.com/egdcrypto/bank-of-z/issues/454";
        when(gitHubService.createIssue(anyString(), anyString())).thenReturn(expectedUrl);

        ValidationReportedEvent event = new ValidationReportedEvent(
            "v-force-360-1",
            "GitHub URL missing in Slack body",
            "LOW",
            java.time.Instant.now()
        );

        // When
        handler.handle(event);

        // Then
        verify(gitHubService, times(1)).createIssue(
            contains("GitHub URL missing in Slack body"), 
            contains("LOW")
        );
    }

    @Test
    void shouldFailIfGitHubServiceReturnsNull() {
        // Given - Simulate the "Actual Behavior" where we don't get a URL
        when(gitHubService.createIssue(anyString(), anyString())).thenReturn(null);

        ValidationReportedEvent event = new ValidationReportedEvent(
            "v-force-360-1", "Fail test", "HIGH", java.time.Instant.now()
        );

        // When & Then - The handler should crash or signal failure because the URL is missing
        assertThrows(IllegalStateException.class, () -> handler.handle(event));
    }
}