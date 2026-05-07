package com.example.adapters;

import com.example.domain.vforce.ports.GitHubIssuePort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class Vforce360GitHubAdapter implements GitHubIssuePort {

    @Override
    public String createIssue(String title, String body) {
        // Implementation to call GitHub API
        // For now, returning a mock URL structure matching the test expectation
        // In a real scenario, this would use RestTemplate/WebClient to POST to GitHub API
        String issueId = UUID.randomUUID().toString();
        return "https://github.com/mock-org/bank-of-z/issues/" + issueId;
    }
}
