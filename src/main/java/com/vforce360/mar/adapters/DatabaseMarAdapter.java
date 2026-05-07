package com.vforce360.mar.adapters;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vforce360.mar.ports.MarRepositoryPort;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Adapter implementation for MAR data retrieval using MongoDB.
 * This serves as the concrete implementation for the MarRepositoryPort.
 */
@Component
public class DatabaseMarAdapter implements MarRepositoryPort {

    private final MongoClient mongoClient;

    @Autowired
    public DatabaseMarAdapter(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    public Optional<String> findByProjectId(UUID projectId) {
        MongoDatabase database = mongoClient.getDatabase("vforce360");
        MongoCollection<Document> collection = database.getCollection("modernization_reports");
        
        Document query = new Document("projectId", projectId.toString());
        Document result = collection.find(query).first();

        if (result != null) {
            // Returning the JSON content as a string to be parsed/rendered later
            return Optional.of(result.toJson());
        }
        return Optional.empty();
    }
}
