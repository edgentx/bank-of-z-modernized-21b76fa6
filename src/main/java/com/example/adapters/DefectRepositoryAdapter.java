package com.example.adapters;

import com.example.domain.shared.Command;
import com.example.ports.DefectRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * Real adapter for Defect Repository.
 * Persists defect reports to MongoDB (VForce360 shared instance).
 */
@Component
@ConditionalOnProperty(name = "defect.repository.mongo.enabled", havingValue = "true", matchIfMissing = false)
public class DefectRepositoryAdapter implements DefectRepositoryPort {

    private static final Logger logger = LoggerFactory.getLogger(DefectRepositoryAdapter.class);
    private final MongoTemplate mongoTemplate;

    public DefectRepositoryAdapter(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void recordDefect(String defectId, Command command) {
        // In a real Temporal/Event Sourcing scenario, this might append to an event store.
        // Here we record the command intent into MongoDB for audit purposes.
        try {
            DefectDocument doc = new DefectDocument(defectId, command);
            mongoTemplate.save(doc);
            logger.info("Recorded defect {} to MongoDB", defectId);
        } catch (Exception e) {
            logger.error("Failed to record defect {}: {}", defectId, e.getMessage());
            // Depending on consistency requirements, we might throw here.
            // For now, we log to avoid blocking the Slack notification in the happy path.
        }
    }

    // Simple document representation for MongoDB storage
    private record DefectDocument(String id, Command command) {}
}
