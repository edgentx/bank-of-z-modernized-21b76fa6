package com.example.mocks;

import com.example.ports.TemporalWorkflowPort;
import com.example.domain.vforce.model.StartVW454ValidationCmd;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Mock implementation of TemporalWorkflowPort for testing.
 * Allows simulating workflow completion without a real Temporal server.
 */
@Component
public class MockTemporalWorkflowAdapter implements TemporalWorkflowPort {

    private StartVW454ValidationCmd lastCommand;

    @Override
    public CompletableFuture<String> executeReportDefectWorkflow(StartVW454ValidationCmd cmd) {
        this.lastCommand = cmd;
        // Return a future that the test can complete manually
        return new CompletableFuture<>();
    }

    public StartVW454ValidationCmd getLastCommand() {
        return lastCommand;
    }
}