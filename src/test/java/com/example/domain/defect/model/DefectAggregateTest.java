package com.example.domain.defect.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefectAggregateTest {

    @Test
    void reportDefect_validCommand_success() {
        String defectId = "d-123";
        DefectAggregate aggregate = new DefectAggregate(defectId);
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, "UI glitch", "Button misaligned");

        List<DomainEvent> events = aggregate.execute(cmd);

        assertFalse(events.isEmpty());
        assertTrue(events.get(0) instanceof DefectReportedEvent);
        assertEquals("UI glitch", aggregate.getTitle());
    }

    @Test
    void linkGitHubIssue_validCommand_success() {
        String defectId = "d-456";
        DefectAggregate aggregate = new DefectAggregate(defectId);
        // Report first
        aggregate.execute(new ReportDefectCmd(defectId, "Bug", "Desc"));
        aggregate.clearEvents(); // Clear uncommitted events

        LinkGitHubIssueCmd cmd = new LinkGitHubIssueCmd(defectId, "https://github.com/example/repo/issues/123");
        List<DomainEvent> events = aggregate.execute(cmd);

        assertFalse(events.isEmpty());
        assertTrue(events.get(0) instanceof GitHubIssueLinkedEvent);
        assertEquals("https://github.com/example/repo/issues/123", aggregate.getGithubIssueUrl());
    }

    @Test
    void linkGitHubIssue_whenNotReported_throwsException() {
        String defectId = "d-789";
        DefectAggregate aggregate = new DefectAggregate(defectId);
        LinkGitHubIssueCmd cmd = new LinkGitHubIssueCmd(defectId, "https://github.com/...");

        assertThrows(IllegalStateException.class, () -> aggregate.execute(cmd));
    }
}