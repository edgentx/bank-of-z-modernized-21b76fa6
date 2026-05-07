package com.vforce360.adapters;

import com.vforce360.mar.models.ModernizationAssessmentReport;
import com.vforce360.ports.ModernizationReportPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * MongoDB implementation of ModernizationReportPort.
 * Retrieves MAR data from the MongoDB database.
 */
public class MongoModernizationReportAdapter implements ModernizationReportPort {

    private final MongoTemplate mongoTemplate;

    /**
     * Constructor for dependency injection.
     * @param mongoTemplate The MongoTemplate instance for database operations.
     */
    public MongoModernizationReportAdapter(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Retrieves the report for a given project ID.
     * 
     * @param projectId The unique identifier of the project.
     * @return The ModernizationAssessmentReport object.
     * @throws org.springframework.data.mongodb.core.MongoDataIntegrityViolationException if not found (optional behavior)
     * or returns null depending on strictness. Here we assume the document exists.
     */
    @Override
    public ModernizationAssessmentReport getReport(String projectId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("projectId").is(projectId));
        return mongoTemplate.findOne(query, ModernizationAssessmentReport.class);
    }
}
