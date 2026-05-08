package com.example.domain.reconciliation.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Reconciliation aggregate.
 * Handles balance verification and defect reporting (S-FB-1).
 */
public class ReconciliationBatch extends AggregateRoot {
    private final String batchId;
    private Status status = Status.NONE;

    public enum Status { NONE, STARTED, BALANCED, FAILED }

    public ReconciliationBatch(String batchId) { this.batchId = batchId; }
    @Override public String id() { return batchId; }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof ForceBalanceCmd c) return forceBalance(c);
        if (cmd instanceof ReportDefectCmd c) return reportDefect(c);
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> forceBalance(ForceBalanceCmd c) {
        if (status != Status.NONE) throw new IllegalStateException("Batch already started");
        var event = new ReconciliationBalancedEvent(batchId, c.expectedSum(), c.actualSum(), Instant.now());
        this.status = Status.BALANCED;
        addEvent(event); incrementVersion();
        return List.of(event);
    }

    private List<DomainEvent> reportDefect(ReportDefectCmd c) {
        if (status == Status.BALANCED) throw new IllegalStateException("Cannot report defect on balanced batch");
        
        // S-FB-1: Construct Slack body ensuring GitHub URL is present
        String slackBody = String.format(
            "Defect Detected in Batch: %s\nReason: %s\nGitHub Issue: <%s>",
            c.batchId(), 
            c.reason() != null ? c.reason() : "Unknown", 
            c.githubIssueUrl()
        );

        var event = new DefectReportedEvent(batchId, batchId, slackBody, c.githubIssueUrl());
        addEvent(event); incrementVersion();
        return List.of(event);
    }

    // Getters for testing
    public Status getStatus() { return status; }
}