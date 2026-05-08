package com.example.ports;

import com.example.domain.vforce360.model.DefectAggregate;

/**
 * Port interface for GitHub interactions.
 */
public interface GitHubClient {
    String createIssue(DefectAggregate defect);
}