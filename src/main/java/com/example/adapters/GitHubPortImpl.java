package com.example.adapters;

import com.example.ports.GitHubPort;
import org.springframework.stereotype.Service;

@Service
public class GitHubPortImpl implements GitHubPort {
    @Override
    public String createIssue(String title, String description) {
        throw new UnsupportedOperationException("Not implemented in TDD Red phase");
    }
}
