package com.vforce360.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Domain Entity: Modernization Assessment Report (MAR).
 * FIXED: Added Getters/Setters required by adapters.
 */
@Entity
@Table(name = "MAR_REPORTS")
public class ModernizationAssessmentReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id", unique = true, nullable = false)
    private String projectId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "raw_markdown", nullable = false)
    private String rawMarkdown; // Renamed for clarity from 'content' in some contexts

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Default Constructor for JPA
    public ModernizationAssessmentReport() {}

    // Business Constructor
    public ModernizationAssessmentReport(String projectId, String title, String rawMarkdown) {
        this.projectId = projectId;
        this.title = title;
        this.rawMarkdown = rawMarkdown;
        this.createdAt = LocalDateTime.now();
    }

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getRawMarkdown() { return rawMarkdown; }
    public void setRawMarkdown(String rawMarkdown) { this.rawMarkdown = rawMarkdown; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
