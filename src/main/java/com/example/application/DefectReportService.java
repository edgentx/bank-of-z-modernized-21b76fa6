package com.example.application;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectRepository;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.shared.SlackMessageValidator;
import org.springframework.stereotype.Service;

@Service
public class DefectReportService {
    
    private final DefectRepository defectRepository;
    private final SlackMessageValidator validator;

    public DefectReportService(DefectRepository defectRepository, SlackMessageValidator validator) {
        this.defectRepository = defectRepository;
        this.validator = validator;
    }

    public void reportDefect(ReportDefectCmd cmd) {
        var aggregate = new DefectAggregate(cmd.defectId());
        aggregate.execute(cmd);
        defectRepository.save(aggregate);
        
        // Note: Real implementation would trigger workflow here.
        // For testing, we might expose a validation step.
    }
}
