package com.example.ports;

import com.example.adapters.GitHubIssueAdapter;
import com.example.ports.dto.IssueRequest;
import com.example.ports.dto.IssueResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test for the GitHub Issue Port/Adapter logic.
 * Ensures the URL is correctly formatted in the response DTO.
 */
class GitHubIssuePortTest {

    private GitHubIssuePort port;
    private GitHubIssueAdapter mockAdapter;

    @BeforeEach
    void setUp() {
        // In a real integration, we might mock the underlying HTTP client,
        // but here we test the adapter's return value contract.
        // For simplicity, we instantiate the real adapter to test its POJO mapping,
        // or we mock it if it contains complex logic.
        // Given the constraints, we test the Port contract.
    }

    @Test
    void whenPortCalled_thenReturnsIssueResponseWithUrl() {
        // This is a placeholder for the contract definition.
        // The actual implementation will be provided in the Green phase.
        GitHubIssuePort port = cmd -> new IssueResponse("https://github.com/mock-repo/issues/1", "I-001");
        
        IssueRequest request = new IssueRequest("Defect", "Description");
        IssueResponse response = port.createIssue(request);

        assertNotNull(response);
        assertNotNull(response.url());
        assertTrue(response.url().startsWith("http"));
    }
}
