package com.example.service;

import com.example.domain.shared.Command;
import java.time.Instant;
import java.util.Objects;

/**
 * Command to report a defect.
 */
public class ReportDefectCmd implements Command {
    private final String ticketId;
    private final String title;
    private final String severity;
    private final String component;
    private final Instant reportedAt;

    public ReportDefectCmd(String ticketId, String title, String severity, String component, Instant reportedAt) {
        this.ticketId = ticketId;
        this.title = title;
        this.severity = severity;
        this.component = component;
        this.reportedAt = reportedAt;
    }

    public String ticketId() { return ticketId; }
    public String title() { return title; }
    public String severity() { return severity; }
    public String component() { return component; }
    public Instant reportedAt() { return reportedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportDefectCmd that = (ReportDefectCmd) o;
        return Objects.equals(ticketId, that.ticketId) && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticketId, title);
    }
}
