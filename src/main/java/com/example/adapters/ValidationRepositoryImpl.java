package com.example.adapters;

import com.example.domain.ports.ValidationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of ValidationRepository.
 * Stores the mapping of a local defect ID to the external GitHub URL.
 * Uses an in-memory store for this phase (MongoDB would be the real implementation).
 */
@Component
public class ValidationRepositoryImpl implements ValidationRepository {

    private static final Logger log = LoggerFactory.getLogger(ValidationRepositoryImpl.class);
    // In-memory store simulating MongoDB persistence
    private final Map<String, String> storage = new ConcurrentHashMap<>();

    @Override
    public CompletableFuture<Void> saveMapping(String defectId, String githubUrl) {
        return CompletableFuture.runAsync(() -> {
            log.info("Saving mapping: Defect {} -> GitHub URL {}", defectId, githubUrl);
            storage.put(defectId, githubUrl);
        });
    }

    @Override
    public CompletableFuture<String> findGithubUrl(String defectId) {
        return CompletableFuture.supplyAsync(() -> storage.get(defectId));
    }
}