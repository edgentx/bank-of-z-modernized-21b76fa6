package com.example.ports;

/**
 * Port interface for reporting defects (e.g., creating GitHub issues).
 * This decouples the domain logic from the specific implementation of issue tracking.
 */
public interface DefectReporterPort {

    /**
     * Records a defect report and returns the URL to the created issue.
     *
     * @param title   The title of the defect.
     * @param details The details/body of the defect.
     * @return The URL of the created GitHub issue.
     */
    String reportDefect(String title, String details);
}
