package com.vforce360.adapters;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vforce360.ports.MarPort;
import org.bson.Document;
import org.springframework.stereotype.Component;

/**
 * MongoDB Adapter for MAR data.
 * Implements the MarPort interface to retrieve raw JSON from MongoDB.
 */
@Component
public class MarMongoAdapter implements MarPort {

    private final MongoClient mongoClient;
    private static final String DB_NAME = "vforce360";
    private static final String COLLECTION_NAME = "mar_reports";

    public MarMongoAdapter(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    public String getMarContent(String projectId) {
        MongoDatabase database = mongoClient.getDatabase(DB_NAME);
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
        
        Document query = new Document("projectId", projectId);
        Document result = collection.find(query).first();

        if (result == null) {
            throw new IllegalArgumentException("Project not found: " + projectId);
        }

        // Returns the raw JSON content stored in the 'content' field
        return result.getString("content");
    }
}
