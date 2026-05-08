package com.example.domain.reconciliation.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Aggregate for handling Reconciliation Batches.
 * Handles defect reporting logic required for S-FB-1.
 */
public class ReconciliationBatch extends AggregateRoot {

    private final String batchId;
    private boolean defectReported;

    public ReconciliationBatch(String batchId) {
        this.batchId = batchId;
    }

    @Override
    public String id() {
        return batchId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof ReportDefectCommand c) {
            return reportDefect(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> reportDefect(ReportDefectCommand cmd) {
        if (defectReported) {
            throw new IllegalStateException("Defect already reported for batch: " + cmd.batchId());
        }
        // Validation logic
        if (cmd.batchId() == null || cmd.batchId().isBlank()) {
            throw new IllegalArgumentException("batchId required");
        }
        if (cmd.discrepancyAmount() == null) {
            throw new IllegalArgumentException("discrepancyAmount required");
        }

        // Create event
        var event = new DefectReportedEvent(
            cmd.batchId(),
            cmd.sourceSystem(),
            cmd.discrepancyAmount(),
            cmd.reason(),
            Instant.now()
        );

        this.defectReported = true;
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }
}