package com.example.mocks;

import com.example.vforce.adapter.TemporalActivityPort;

/**
 * Mock implementation of TemporalActivityPort.
 * Used to simulate the behavior of the Temporal workflow in a unit test environment.
 */
public class MockTemporalActivity implements TemporalActivityPort {

    private final String simulatedResponseBody;
    private boolean executed = false;

    public MockTemporalActivity(String simulatedResponseBody) {
        this.simulatedResponseBody = simulatedResponseBody;
    }

    @Override
    public String executeReportDefect() {
        this.executed = true;
        // Simulate the logic that would format the Slack body
        return simulatedResponseBody;
    }

    public boolean wasExecuted() {
        return executed;
    }
}
