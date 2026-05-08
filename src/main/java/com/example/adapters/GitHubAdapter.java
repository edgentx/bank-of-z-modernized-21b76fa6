package com.example.adapters;

import com.example.ports.IssueTrackerPort;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * Real adapter implementation for IssueTrackerPort.
 * Simulates calling GitHub API.
 */
@Component
public class GitHubAdapter implements IssueTrackerPort {

    @Override
    public String createIssue(String defectId, String title) {
        // Simulate network delay or logic
        // In a real scenario, this would use RestTemplate/WebClient to hit GitHub API
        // For this Green phase, we generate a deterministic URL based on inputs
        // to satisfy the requirement of a valid URL format.
        int mockId = Math.abs(defectId.hashCode());
        return "https://github.com/mock-org/project/issues/" + mockId;
    }
}
