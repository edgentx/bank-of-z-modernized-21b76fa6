package com.example.domain.validation.service;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * Service for validating defect reports and associated metadata.
 */
@Service
public class ValidationService {

    private static final Pattern GITHUB_ISSUE_URL_PATTERN = Pattern.compile("https://github.com/[a-zA-Z0-9-]+/issues/[0-9]+");

    /**
     * Validates if the provided string is a valid GitHub Issue URL.
     * Supports defect VW-454 validation logic.
     * 
     * @param url The URL string to validate.
     * @return true if valid, false otherwise.
     */
    public boolean isValidGitHubIssueUrl(String url) {
        if (url == null) return false;
        return GITHUB_ISSUE_URL_PATTERN.matcher(url).matches();
    }
}
