package com.example.domain.vforce360.service;

import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.domain.vforce360.model.VForce360Aggregate;
import com.example.ports.VForce360Repository;
import org.springframework.stereotype.Service;

@Service
public class VForce360Service {

    private final VForce360Repository repository;

    public VForce360Service(VForce360Repository repository) {
        this.repository = repository;
    }

    public void handleReportDefect(ReportDefectCmd cmd) {
        // NOTE: The main orchestration is currently in DefectReportingService.
        // This service can act as a domain-specific facade if needed later,
        // or handle queries. For now, it satisfies the compiler/spring context requirements.
    }
}
