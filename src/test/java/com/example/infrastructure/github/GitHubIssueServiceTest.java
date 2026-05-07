package com.example.infrastructure.github;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.ports.GitHubPort;
import com.example.infrastructure.config.GitHubProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for creating GitHub Issues.
 */
class GitHubIssueServiceTest {

    private GitHubPort gitHubPort;
    private GitHubProperties properties;
    private GitHubIssueService service;

    @BeforeEach
    void setUp() {
        gitHubPort = mock(GitHubPort.class);
        properties = new GitHubProperties();
        properties.setBaseUrl("https://github.com/test/");
        service = new GitHubIssueService(gitHubPort, properties);
    }

    @Test
    void shouldCreateIssueViaPort() {
        // Given
        ReportDefectCmd cmd = new ReportDefectCmd("ID-1", "Title", "Desc");
        when(gitHubPort.createIssue(anyString(), anyString())).thenReturn("https://github.com/test/ID-1");

        // When
        String url = service.createIssue(cmd);

        // Then
        assertNotNull(url);
        assertTrue(url.contains("ID-1"));
        verify(gitHubPort).createIssue("Title", "Desc");
    }
}
