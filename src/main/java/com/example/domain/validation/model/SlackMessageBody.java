package com.example.domain.validation.model;

import com.example.domain.shared.ValueObject;
import java.util.Objects;

/**
 * Value Object representing the body of a Slack message.
 * This is the output of the ReportDefectWorkflow.
 */
public class SlackMessageBody implements ValueObject {
    private final String body;

    public SlackMessageBody(String body) {
        if (body == null || body.isBlank()) {
            throw new IllegalArgumentException("Slack body cannot be null or blank");
        }
        this.body = body;
    }

    public String value() {
        return body;
    }

    /**
     * Helper to verify if the body contains the specific GitHub URL.
     * Corresponds to "Verify Slack body contains GitHub issue link".
     */
    public boolean containsUrl(GitHubIssueUrl issueUrl) {
        return body.contains(issueUrl.value());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlackMessageBody that = (SlackMessageBody) o;
        return Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(body);
    }

    @Override
    public String toString() {
        return body;
    }
}
