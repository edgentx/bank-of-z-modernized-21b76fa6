package com.example.ports;

import com.example.domain.shared.Command;

/**
 * Port for triggering Temporal workflows related to defects.
 * This isolates the application service from the Temporal SDK.
 */
public interface TemporalDefectPort {
    void reportDefect(Command command);
}
