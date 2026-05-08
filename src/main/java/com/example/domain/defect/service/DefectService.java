package com.example.domain.defect.service;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCommand;
import com.example.defect.repository.DefectRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Application Service for Defect Reporting.
 * Orchestrates the execution of commands on the Defect Aggregate and persistence.
 */
@Service
public class DefectService {

    private final DefectRepository defectRepository;

    public DefectService(DefectRepository defectRepository) {
        this.defectRepository = defectRepository;
    }

    /**
     * Reports a new defect.
     * Creates the aggregate, executes the command, and persists the result.
     *
     * @param cmd The command containing defect details.
     * @return The ID of the created defect.
     */
    public String reportDefect(ReportDefectCommand cmd) {
        // Ensure we have an ID if not provided
        String defectId = cmd.defectId() != null ? cmd.defectId() : "DEFECT-" + UUID.randomUUID().toString().substring(0, 8);
        
        // Finalize command ID for consistency if it was null
        ReportDefectCommand finalCmd = new ReportDefectCommand(defectId, cmd.title(), cmd.description(), cmd.githubIssueUrl());

        DefectAggregate aggregate = new DefectAggregate(defectId);
        aggregate.execute(finalCmd);

        defectRepository.save(aggregate);

        return defectId;
    }
}
