package com.example.mocks;

import com.example.domain.shared.Command;
import com.example.ports.DefectRepository;

/**
 * Mock implementation of DefectRepository for testing.
 */
public class MockDefectRepository implements DefectRepository {
    @Override
    public void save(Command command) {
        // No-op in memory mock
    }
}