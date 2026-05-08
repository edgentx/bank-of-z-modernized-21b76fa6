package com.example.domain.defect.service;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.defect.repository.DefectRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DefectService {

    private final DefectRepository defectRepository;

    public DefectService(DefectRepository defectRepository) {
        this.defectRepository = defectRepository;
    }

    /**
     * Handles the logic to report a defect.
     * Corresponds to Triggering _report_defect via temporal-worker exec.
     */
    public DefectAggregate reportDefect(String title, String description, String githubUrl) {
        String defectId = UUID.randomUUID().toString();
        DefectAggregate aggregate = new DefectAggregate(defectId);
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, title, description, githubUrl, null);
        
        aggregate.execute(cmd);
        defectRepository.save(aggregate);
        
        return aggregate;
    }
}
