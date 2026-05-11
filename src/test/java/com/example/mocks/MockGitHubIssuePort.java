package com.example.mocks;

import com.example.ports.GitHubIssuePort;
import com.example.domain.shared.ReportDefectCmd;
import org.springframework.stereotype.Component;

/**
 * Mock implementation of GitHubIssuePort for testing.
 * Returns a predictable URL without calling the network.
 */
@Component
public class MockGitHubIssuePort implements GitHubIssuePort {

    private final String mockBaseUrl = "https://github.com/egdcrypto/bank-of-z/issues/";
    private int issueCount = 1;

    @Override
    public String createIssue(ReportDefectCmd cmd) {
        // Simulate the behavior of creating an issue
        String url = mockBaseUrl + issueCount++;
        // In a real mock scenario, we might want to store the cmd to verify it later
        return url;
    }

    public void reset() {
        issueCount = 1;
    }

    public String getMockUrl(int index) {
        return mockBaseUrl + index;
    }
}
