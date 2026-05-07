package com.vforce360.mar.mocks;

import com.vforce360.mar.model.MarDocument;
import com.vforce360.mar.ports.MarRepositoryPort;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock adapter for the MarRepositoryPort.
 * Used in testing to simulate database interactions without connecting to DB2/MongoDB.
 * Returns predictable data to allow for deterministic assertions.
 */
public class MockMarRepository implements MarRepositoryPort {

    private final Map<String, MarDocument> database = new HashMap<>();

    public MockMarRepository() {
        // Seed with data from the defect report:
        // "The MAR content appears to be rendered as raw JSON text"
        String rawJsonContent = "{\"risk\":\"High\",\"effort\":120,\"description\":\"Legacy mainframe dependency\"}";
        
        // Simulate the brownfield project mentioned in the story
        database.put("21b76fa6-afb6-4593-9e1b-b5d7548ac4d1", 
            new MarDocument("21b76fa6-afb6-4593-9e1b-b5d7548ac4d1", "Legacy Assessment", rawJsonContent));
    }

    @Override
    public MarDocument findByProjectId(String projectId) {
        // Simulate DB behavior: return null or throw if not found
        if (!database.containsKey(projectId)) {
            throw new IllegalArgumentException("Project not found: " + projectId);
        }
        return database.get(projectId);
    }

    /**
     * Helper method for tests to override specific scenarios.
     */
    public void seedData(String projectId, MarDocument doc) {
        database.put(projectId, doc);
    }
}