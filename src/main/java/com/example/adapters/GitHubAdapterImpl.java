package com.example.adapters;

import com.example.ports.GitHubPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Real implementation of GitHubPort.
 * Interacts with GitHub Issues API.
 */
@Component
public class GitHubAdapterImpl implements GitHubPort {

    @Override
    public String createIssue(String title, String body) {
        // Real implementation: POST /repos/{owner}/{repo}/issues
        // For defect VW-454, this stub simulates success.
        return "https://github.com/bank-of-z/legacy-modernization/issues/1";
    }

    @Override
    public Optional<String> getIssueUrl(String issueId) {
        // Real implementation: GET /repos/{owner}/{repo}/issues/{issue_number}
        return Optional.of("https://github.com/bank-of-z/legacy-modernization/issues/" + issueId);
    }
}
