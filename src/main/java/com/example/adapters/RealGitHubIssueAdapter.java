package com.example.adapters;

import com.example.domain.validation.model.ValidationAggregate;
import com.example.ports.GitHubPort;
import org.springframework.stereotype.Component;

@Component
public class RealGitHubIssueAdapter implements GitHubPort {
    @Override
    public String createIssue(ValidationAggregate aggregate) {
        // Simulated implementation for compile success
        return "https://github.com/fake/issues/1";
    }
}
