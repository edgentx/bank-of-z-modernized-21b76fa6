package com.example.mocks;

import com.example.ports.GithubPort;
import com.example.vforce.github.GithubIssue;
import com.example.vforce.shared.ReportDefectCommand;

import java.util.Optional;

/**
 * Mock implementation of GithubPort for testing.
 * Allows simulating success or failure of issue creation.
 */
public class MockGithubPort implements GithubPort {

    private GithubIssue nextResult = null;
    private boolean shouldFail = false;

    public void setNextIssue(GithubIssue issue) {
        this.nextResult = issue;
        this.shouldFail = false;
    }

    public void setNextCreateToFail() {
        this.shouldFail = true;
    }

    @Override
    public Optional<GithubIssue> createIssue(ReportDefectCommand cmd) {
        if (shouldFail) {
            return Optional.empty();
        }
        return Optional.ofNullable(nextResult);
    }
}
