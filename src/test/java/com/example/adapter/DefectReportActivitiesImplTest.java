package com.example.adapter;

import com.example.ports.SlackPort;
import com.example.ports.GitHubPort;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TDD Red Phase Test for S-FB-1: Validating VW-454.
 *
 * Verifies that when a defect is reported, the resulting Slack notification
 * contains the GitHub issue URL in the message body.
 */
class DefectReportActivitiesImplTest {

    private SlackPort slackPort;
    private GitHubPort gitHubPort;
    private ObservationRegistry observationRegistry;
    private DefectReportActivitiesImpl activities;

    @BeforeEach
    void setUp() {
        slackPort = Mockito.mock(SlackPort.class);
        gitHubPort = Mockito.mock(GitHubPort.class);
        observationRegistry = ObservationRegistry.NOOP; // Use NOOP for unit tests

        activities = new DefectReportActivitiesImpl(slackPort, gitHubPort, observationRegistry);
    }

    @Test
    void testReportDefect_shouldPostToSlackWithGitHubUrl() {
        // Arrange
        String defectTitle = "VW-454: Missing GitHub URL";
        String defectBody = "Repro steps...";
        String expectedGitHubUrl = "https://github.com/example-org/bank-of-z/issues/454";

        // Stub GitHub port to return a valid URL upon creation
        when(gitHubPort.createIssue(anyString(), anyString()))
            .thenReturn(Optional.of(URI.create(expectedGitHubUrl)));

        // Act
        activities.reportDefect(defectTitle, defectBody);

        // Assert
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(slackPort).sendMessage(messageCaptor.capture());

        String actualSlackMessage = messageCaptor.getValue();
        
        // This assertion ensures the defect is fixed: The URL must be present in the body.
        assertTrue(
            actualSlackMessage.contains(expectedGitHubUrl),
            "Expected Slack body to contain GitHub URL: " + expectedGitHubUrl + " but was: " + actualSlackMessage
        );
    }
}
