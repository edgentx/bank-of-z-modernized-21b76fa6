package com.example.domain.defect;

/**
 * Activity interface for reporting defects.
 * Defines the contract for the implementation that orchestrates
 * persistence and notifications.
 */
public interface ReportDefectActivity {

    /**
     * Reports a defect to the VForce360 system.
     * This activity captures the defect, generates the GitHub URL,
     * and triggers the Slack notification.
     *
     * @param title The title of the defect.
     * @param description The detailed description of the defect.
     * @return The generated GitHub URL for the created issue.
     */
    String execute(String title, String description);
}