package com.example.adapters;

import com.example.ports.IssueTrackerPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Real-world implementation of the Issue Tracker port.
 * This would typically query GitHub Issues API or Jira API.
 * As a placeholder for the defect fix, it returns empty,
 * allowing the domain logic to handle the "Not Found" path.
 */
@Component
public class IssueTrackerAdapter implements IssueTrackerPort {

    @Override
    public Optional<String> getIssueUrl(String issueId) {
        // TODO: Implement actual GitHub/Jira API call
        // e.g., return Optional.of(gitlabClient.getIssueUrl(issueId));
        // Returning empty simulates a lookup failure for the real adapter context
        return Optional.empty();
    }
}