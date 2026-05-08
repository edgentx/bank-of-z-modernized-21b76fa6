package com.example.adapters;

import com.example.ports.VForce360Repository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * MongoDB Adapter for VForce360 shared state.
 */
@Repository
public class VForce360MongoAdapter implements VForce360Repository {

    private final MongoTemplate mongoTemplate;

    public VForce360MongoAdapter(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void saveData(Object data) {
        mongoTemplate.save(data, "vforce360_data");
    }
}
