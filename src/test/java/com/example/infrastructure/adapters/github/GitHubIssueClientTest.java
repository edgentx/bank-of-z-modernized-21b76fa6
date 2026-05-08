package com.example.infrastructure.adapters.github;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import com.example.infrastructure.adapters.github.GitHubIssueClient.IssueRequest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integration test for GitHubIssueClient.
 * Story: S-FB-1
 * TDD Phase: RED (Dependency setup verification)
 */
@SpringBootTest(classes = {GitHubIssueClient.class})
@AutoConfigureWireMock(port = 8089)
@ActiveProfiles("test")
class GitHubIssueClientTest {

    @Autowired
    private GitHubIssueClient gitHubIssueClient;

    @Test
    void should_create_issue_via_github_api() {
        // Given
        stubFor(post(urlEqualTo("/repos/example/bank-of-z/issues"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"html_url\":\"https://github.com/example/bank-of-z/issues/454\"}")));

        IssueRequest request = new IssueRequest("Defect VW-454", "Body");

        // When
        String url = gitHubIssueClient.createIssue(request);

        // Then
        assertNotNull(url);
        assertEquals("https://github.com/example/bank-of-z/issues/454", url);
    }
}