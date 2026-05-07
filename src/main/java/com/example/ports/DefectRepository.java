package com.example.ports;

import com.example.domain.shared.Command;

/**
 * Port for persisting defect reports.
 * Mocked in tests to prevent real DB connections.
 */
public interface DefectRepository {
    void save(Command command);
}