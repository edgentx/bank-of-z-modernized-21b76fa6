package com.example.mocks;

import com.example.ports.VForce360Port;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory mock implementation of VForce360Port.
 * Simulates the external VForce360 API response without network calls.
 */
public class InMemoryVForce360Port implements VForce360Port {

    private final Map<String, String> database = new HashMap<>();
    private boolean shouldFail = false;
    private String lastTitle;

    @Override
    public String reportDefect(DefectRequest request) {
        this.lastTitle = request.title();
        if (shouldFail) {
            throw new RuntimeException("VForce360 API unavailable");
        }
        
        // Simulate generating a valid GitHub/VForce360 issue URL
        // In a real scenario, this might return a GUID or a specific ticket number.
        String mockUrl = "https://github.com/bank-of-z/vforce360/issues/" + System.currentTimeMillis();
        database.put(request.title(), mockUrl);
        return mockUrl;
    }

    public String getUrlFor(String title) {
        return database.get(title);
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }
}
