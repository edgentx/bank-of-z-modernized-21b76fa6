package com.example.adapters;

import com.example.domain.validation.model.IssueUrl;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.repository.ValidationRepository;
import com.example.ports.IssueTrackerPort;
import com.example.ports.NotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * TDD Red Phase Test for Story S-FB-1.
 * Validates that the GitHub URL is present in the Slack notification body.
 */
class ValidationServiceTest {

    private ValidationRepository repository;
    private IssueTrackerPort issueTracker;
    private NotificationPort notificationPort;
    private ValidationService service;

    private static final String VALIDATION_ID = "VW-454";
    private static final String GITHUB_URL = "https://github.com/example/issues/454";

    @BeforeEach
    void setUp() {
        repository = mock(ValidationRepository.class);
        issueTracker = mock(IssueTrackerPort.class);
        notificationPort = mock(NotificationPort.class);
        service = new ValidationService(repository, issueTracker, notificationPort);
    }

    @Test
    void reportDefect_shouldCreateGitHubIssue() {
        // Given
        ValidationAggregate aggregate = new ValidationAggregate(VALIDATION_ID);
        aggregate.execute(new ReportDefectCmd(VALIDATION_ID, "Critical validation failure"));
        when(repository.findById(VALIDATION_ID)).thenReturn(aggregate);
        when(issueTracker.createIssue(any(), any())).thenReturn(new IssueUrl(GITHUB_URL));

        // When
        service.reportDefect(VALIDATION_ID);

        // Then
        verify(issueTracker).createIssue(contains(VALIDATION_ID), contains(VALIDATION_ID));
    }

    @Test
    void reportDefect_shouldUpdateAggregateWithUrl() {
        // Given
        ValidationAggregate aggregate = new ValidationAggregate(VALIDATION_ID);
        aggregate.execute(new ReportDefectCmd(VALIDATION_ID, "Defect"));
        when(repository.findById(VALIDATION_ID)).thenReturn(aggregate);
        when(issueTracker.createIssue(any(), any())).thenReturn(new IssueUrl(GITHUB_URL));

        // When
        service.reportDefect(VALIDATION_ID);

        // Then
        // Verify aggregate state update logic is triggered (implicitly via save)
        verify(repository).save(refEq(aggregate, "uncommittedEvents")); 
        assertThat(aggregate.getGithubIssueUrl()).isEqualTo(GITHUB_URL);
    }

    @Test
    void reportDefect_shouldSendSlackNotificationContainingGitHubUrl() {
        // Given
        ValidationAggregate aggregate = new ValidationAggregate(VALIDATION_ID);
        aggregate.execute(new ReportDefectCmd(VALIDATION_ID, "Failure"));
        when(repository.findById(VALIDATION_ID)).thenReturn(aggregate);
        when(issueTracker.createIssue(any(), any())).thenReturn(new IssueUrl(GITHUB_URL));

        // When
        service.reportDefect(VALIDATION_ID);

        // Then - The Core Assertion for S-FB-1
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificationPort).sendNotification(messageCaptor.capture());

        String sentMessage = messageCaptor.getValue();
        assertThat(sentMessage)
            .as("Slack body must include the exact GitHub URL")
            .contains(GITHUB_URL);
    }

    @Test
    void reportDefect_slackMessageFormatShouldBeHumanReadable() {
        // Given
        ValidationAggregate aggregate = new ValidationAggregate(VALIDATION_ID);
        aggregate.execute(new ReportDefectCmd(VALIDATION_ID, "Missing Auth Header"));
        when(repository.findById(VALIDATION_ID)).thenReturn(aggregate);
        when(issueTracker.createIssue(any(), any())).thenReturn(new IssueUrl(GITHUB_URL));

        // When
        service.reportDefect(VALIDATION_ID);

        // Then
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificationPort).sendNotification(messageCaptor.capture());
        
        assertThat(messageCaptor.getValue()).matches(".*GitHub issue:.*");
    }
}
