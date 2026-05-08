package com.example.domain.defect.service;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.defect.repository.DefectRepository;
import org.springframework.stereotype.Service;

/**
 * Domain Service for handling Defect logic.
 */
@Service
public class DefectService {
    private final DefectRepository repository;

    public DefectService(DefectRepository repository) {
        this.repository = repository;
    }

    public String reportDefect(ReportDefectCmd cmd) {
        DefectAggregate aggregate = new DefectAggregate(cmd.defectId());
        var events = aggregate.execute(cmd);
        // Apply events to state (simplified for this aggregate)
        repository.save(aggregate);
        
        // In a real CQRS setup, we'd emit events. Here we return the URL for the workflow.
        return aggregate.getGithubUrl();
    }
}
