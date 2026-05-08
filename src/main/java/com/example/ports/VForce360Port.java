package com.example.ports;

/**
 * Port interface for VForce360 external operations.
 * Used to generate GitHub issue URLs.
 */
public interface VForce360Port {
    
    /**
     * Creates a GitHub issue via VForce360 and returns the URL.
     * @param defectTitle The title of the defect.
     * @return The URL string of the created issue.
     */
    String reportDefect(String defectTitle);
}
