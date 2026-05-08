package mocks;

import com.example.ports.GitHubPort;
import java.util.Optional;

/**
 * Mock adapter for GitHub interactions.
 * Simulates issue creation and URL generation.
 */
public class MockGitHubPort implements GitHubPort {

    private String nextIssueUrl;
    private boolean shouldFail = false;

    @Override
    public Optional<String> createIssue(String title, String description) {
        if (shouldFail) {
            return Optional.empty();
        }
        // Return a predictable fake URL
        return Optional.ofNullable(nextIssueUrl);
    }

    public void setNextIssueUrl(String url) {
        this.nextIssueUrl = url;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }
}
