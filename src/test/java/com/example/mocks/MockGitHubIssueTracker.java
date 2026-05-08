package com.example.mocks;

import com.example.ports.GitHubIssueTracker;
import com.example.domain.validation.model.ReportDefectCommand;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of GitHubIssueTracker for testing.
 * Allows pre-programming responses without real network I/O.
 */
public class MockGitHubIssueTracker implements GitHubIssueTracker {

    private String fixedUrl = "https://github.com/bank-of-z/default-issue/1";
    private boolean shouldFail = false;

    public void setFixedUrl(String url) {
        this.fixedUrl = url;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }

    @Override
    public String createIssue(ReportDefectCommand cmd) {
        if (shouldFail) {
            return null; // Simulate failure or no link creation
        }
        return fixedUrl;
    }
}
