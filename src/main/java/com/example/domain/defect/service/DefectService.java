package com.example.domain.defect.service;

import com.example.domain.validation.model.ValidationAggregate;
import com.example.ports.ValidationRepositoryPort;
import org.springframework.stereotype.Service;
import java.util.Optional;

/**
 * Service for handling Defect operations.
 * Temporal workflows or controllers can interact with the domain through this service.
 */
@Service
public class DefectService {

    private final ValidationRepositoryPort repository;

    public DefectService(ValidationRepositoryPort repository) {
        this.repository = repository;
    }

    /**
     * Handles the reporting of a defect via the Validation Aggregate.
     * Ensures the GitHub URL is captured (S-FB-1).
     */
    public ValidationAggregate reportDefect(String validationId, String defectId, String title, String githubUrl) {
        Optional<ValidationAggregate> aggregateOpt = repository.findById(validationId);
        
        // If aggregate doesn't exist, we create a new one (simplified for this defect fix).
        // In a full CQRS flow, we might load from history or create new.
        ValidationAggregate aggregate = aggregateOpt.orElseGet(() -> new ValidationAggregate(validationId));

        // Execute the command
        var cmd = new ValidationAggregate.ReportDefectCommand(validationId, defectId, title, githubUrl);
        aggregate.execute(cmd);

        // Save state
        return repository.save(aggregate);
    }
}
