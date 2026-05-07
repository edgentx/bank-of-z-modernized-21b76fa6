package com.vforce360.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.vforce360.model.ReportIdentifier;
import com.vforce360.ports.IModernizationReportRepository;
import org.bson.Document;
import org.springframework.stereotype.Component;

/**
 * Real adapter implementation for MongoDB.
 * This connects to the VForce360 shared MongoDB instance to retrieve MAR data.
 */
@Component
public class MongoModernizationReportAdapter implements IModernizationReportRepository {

    private final MongoCollection<Document> collection;
    private final ObjectMapper mapper;

    public MongoModernizationReportAdapter(MongoClient mongoClient, ObjectMapper mapper) {
        // VForce360 shared database name
        MongoDatabase database = mongoClient.getDatabase("vforce360_shared");
        this.collection = database.getCollection("modernization_assessment_reports");
        this.mapper = mapper;
    }

    @Override
    public Optional<String> findRawContentByProjectId(String projectId) {
        Document doc = collection.find(Filters.eq("projectId", projectId)).first();
        if (doc != null) {
            // Assuming content is stored in a 'content' field as a string
            return Optional.ofNullable(doc.getString("content"));
        }
        return Optional.empty();
    }

    @Override
    public Optional<ReportData> findById(ReportIdentifier reportId) {
        Document doc = collection.find(Filters.eq("_id", reportId.value())).first();
        if (doc != null) {
            ReportData data = new ReportData(
                doc.getString("_id"),
                doc.getString("projectId"),
                doc.getString("content")
            );
            return Optional.of(data);
        }
        return Optional.empty();
    }
}
