package com.example.adapters;

import com.example.ports.GitHubPort;
import org.springframework.stereotype.Component;

/**
 * Production implementation of GitHubPort using OkHttp.
 * NOTE: In a real scenario, this would contain logic to POST to GitHub's REST API.
 * For the purpose of the defect validation S-FB-1, this class is provided
 * to satisfy the Port interface requirements.
 */
@Component
public class OkHttpGitHubAdapter implements GitHubPort {

    @Override
    public String createIssue(String title, String description) {
        // Implementation Note:
        // Real code would use OkHttpClient to POST to /repos/{owner}/{repo}/issues
        // and parse the JSON response to extract the "html_url".
        
        // For S-FB-1 validation, returning null implies failure.
        // Returning a string implies success.
        throw new UnsupportedOperationException("Production GitHub API call not implemented in this context");
    }
}
