package com.example.mocks;

import com.example.ports.GitHubPort;
import org.springframework.stereotype.Component;

@Component
public class MockGitHubAdapter implements GitHubPort {

    private final String baseUrl = "https://github.com/egdcrypto/bank-of-z/issues/";
    private int issueCounter = 100;

    @Override
    public String createIssue(String title, String body) {
        String url = baseUrl + issueCounter++;
        // In a real test, we might verify title/body content here
        return url;
    }
}