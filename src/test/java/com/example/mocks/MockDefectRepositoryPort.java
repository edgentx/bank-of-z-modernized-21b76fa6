package com.example.mocks;

import com.example.domain.shared.Command;
import com.example.ports.DefectRepositoryPort;

/**
 * Mock adapter for Defect Repository.
 */
public class MockDefectRepositoryPort implements DefectRepositoryPort {

    @Override
    public void recordDefect(String defectId, Command command) {
        // No-op for test validation purposes
    }
}
