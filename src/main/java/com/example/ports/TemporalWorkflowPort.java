package com.example.ports;

import com.example.domain.vforce.model.StartVW454ValidationCmd;
import java.util.concurrent.CompletableFuture;

/**
 * Port interface for Temporal Workflow execution.
 */
public interface TemporalWorkflowPort {
    CompletableFuture<String> executeReportDefectWorkflow(StartVW454ValidationCmd cmd);
}