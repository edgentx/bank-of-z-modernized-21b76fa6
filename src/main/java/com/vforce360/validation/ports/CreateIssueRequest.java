package com.vforce360.validation.ports;

import com.vforce360.validation.core.Severity;

/**
 * Request object for creating a GitHub issue.
 */
public class CreateIssueRequest {

    private final String title;
    private final String body;
    private final String label; // Derived from Severity

    public CreateIssueRequest(String title, String description, Severity severity) {
        this.title = title;
        this.body = description;
        this.label = severity.name();
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getLabel() {
        return label;
    }
}
