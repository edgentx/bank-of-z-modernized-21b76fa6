package com.example.services;

import com.example.domain.shared.ReportDefectCmd;
import com.example.domain.shared.ValidationReportedEvent;
import com.example.domain.defect.model.DefectAggregate;
import org.springframework.stereotype.Service;

@Service
public class DefectReportingService {

    public ValidationReportedEvent reportDefect(ReportDefectCmd cmd) {
        DefectAggregate aggregate = new DefectAggregate(cmd.defectId());
        var events = aggregate.execute(cmd);
        if (!events.isEmpty()) {
            return (ValidationReportedEvent) events.get(0);
        }
        throw new RuntimeException("Failed to report defect");
    }
}
