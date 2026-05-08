package com.example.ports;

import com.example.domain.shared.Command;

/**
 * Port for recording defect reports (Temporal or DB interaction).
 */
public interface DefectRepositoryPort {
    void recordDefect(String defectId, Command command);
}
