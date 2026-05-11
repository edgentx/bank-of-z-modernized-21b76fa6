package com.example.ports;

public interface TemporalWorkflowPort {
    void triggerReportDefect(String issueId, String description);
}
