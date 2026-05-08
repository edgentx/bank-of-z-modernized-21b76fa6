package com.example.domain.defect;

/**
 * Port interface for GitHub Issue creation.
 * Moved to domain package to align with DefectAggregate location in the provided stubs.
 * Ideally should be in src/ports/ or src/domain/defect/ports depending on project structure.
 */
public interface GitHubPort {
    String createIssue(String title, String body);
}
