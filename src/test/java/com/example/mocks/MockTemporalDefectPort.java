package com.example.mocks;

import com.example.domain.shared.Command;
import com.example.ports.TemporalDefectPort;

/**
 * Mock adapter for the TemporalDefectPort.
 * Captures workflow signals in memory for verification in tests.
 */
public class MockTemporalDefectPort implements TemporalDefectPort {
    private Command lastCommand;

    @Override
    public void reportDefect(Command command) {
        this.lastCommand = command;
    }

    public Command getLastCommand() {
        return lastCommand;
    }
}
