package com.vforce360.validation.adapters;

import com.vforce360.validation.core.DefectReport;
import com.vforce360.validation.ports.ValidationRepositoryPort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the Validation Repository using MongoDB.
 * This acts as the Adapter wrapping the Spring Data Repository.
 */
@Component
public class ValidationRepositoryAdapter implements ValidationRepositoryPort {

    // In a real Spring Boot app, we might inject a standard MongoRepository here.
    // private final MongoDefectReportRepository mongoRepository;

    public ValidationRepositoryAdapter() {
        // Inject repositories here
    }

    @Override
    public void save(DefectReport report) {
        // mongoRepository.save(report);
        System.out.println("[DB] Saving defect: " + report.getId());
    }
}
