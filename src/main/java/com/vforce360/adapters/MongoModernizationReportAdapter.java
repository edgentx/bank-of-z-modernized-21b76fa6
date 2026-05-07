package com.vforce360.adapters;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;

import com.vforce360.model.MarReport;
import com.vforce360.ports.MarReportPort;

public class MongoModernizationReportAdapter implements MarReportPort {

    private final MongoClient mongoClient;
    private final ObjectMapper mapper;

    public MongoModernizationReportAdapter(MongoClient mongoClient, ObjectMapper mapper) {
        this.mongoClient = mongoClient;
        this.mapper = mapper;
    }

    @Override
    public MarReport findByProjectId(String projectId) {
        MongoDatabase db = mongoClient.getDatabase("vforce360");
        MongoCollection<Document> collection = db.getCollection("modernization_reports");
        
        Document doc = collection.find(new Document("projectId", projectId)).first();
        if (doc == null) {
            return null;
        }
        
        // Convert BSON to POJO
        return mapper.convertValue(doc, MarReport.class);
    }
}
