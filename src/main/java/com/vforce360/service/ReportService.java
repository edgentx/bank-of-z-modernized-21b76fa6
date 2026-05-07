package com.vforce360.service;

import com.vforce360.model.ReportIdentifier;
import com.vforce360.ports.IModernizationReportRepository;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    private final IModernizationReportRepository repository;

    public ReportService(IModernizationReportRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves the Modernization Assessment Report (MAR) for a specific project.
     * This method should return processed content suitable for display, not raw storage strings.
     */
    public String getMarDisplayContent(String projectId) {
        // RED PHASE: Implementation is missing/incorrect.
        // Current defect logic: returning raw JSON directly.
        return repository.findRawContentByProjectId(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found for project: " + projectId));
    }

    /**
     * Retrieves the report by ID.
     */
    public IModernizationReportRepository.ReportData getReportById(ReportIdentifier id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Report not found: " + id.value()));
    }
}
