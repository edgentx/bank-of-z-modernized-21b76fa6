package com.example.domain.defect.service;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCommand;
import com.example.ports.DefectRepository;
import org.springframework.stereotype.Service;

/**
 * Domain Service for handling Defect related commands.
 * This acts as the primary entry point for the Defect aggregate logic.
 */
@Service
public class DefectService {

    private final DefectRepository defectRepository;

    public DefectService(DefectRepository defectRepository) {
        this.defectRepository = defectRepository;
    }

    /**
     * Handles the reporting of a new defect.
     * Creates or loads the aggregate, executes the command, and persists the result.
     *
     * @param cmd The command to report a defect.
     * @return The updated DefectAggregate containing the GitHub URL.
     */
    public DefectAggregate reportDefect(ReportDefectCommand cmd) {
        DefectAggregate aggregate = defectRepository.findById(cmd.defectId())
                .orElse(new DefectAggregate(cmd.defectId()));

        aggregate.execute(cmd);

        defectRepository.save(aggregate);
        return aggregate;
    }
}
