package com.example.mocks;

import com.example.ports.VForce360Client;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of VForce360Client for testing.
 * Tracks reported defects to verify the content (like the GitHub URL) without calling the real service.
 */
public class MockVForce360Client implements VForce360Client {

    private final Map<String, String> reportedBodies = new HashMap<>();

    /**
     * Simulates reporting a defect and stores the generated body.
     * Returns a body containing a valid GitHub URL if the inputs match the known defect (VW-454),
     * otherwise returns an empty string to simulate a defect in the generation logic.
     */
    @Override
    public String reportDefect(String defectTitle, String projectId, String severity) {
        String body = "";
        
        // Simulating the Actual Behavior (The Bug) vs Expected Behavior logic
        // For the purpose of the test, we assume the system *tries* to generate the link,
        // but we need to verify it exists in the output.
        if ("VW-454".equals(defectTitle)) {
            // Simulating the *expected* correct content generation for the happy path
            body = "Defect Reported: " + defectTitle + " - See <https://github.com/org/repo/issues/454>";
        } 
        // If the title is wrong, the system fails to find the link

        reportedBodies.put(defectTitle, body);
        return body;
    }

    public String getLastBodyForTitle(String title) {
        return reportedBodies.get(title);
    }
}
