package com.example.application;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;

import java.util.List;

public class DefectWorkflowService {
    private final DefectAggregate defectAggregate;

    public DefectWorkflowService(DefectAggregate defectAggregate) {
        this.defectAggregate = defectAggregate;
    }

    public List<DomainEvent> process(Command command) {
        return defectAggregate.execute(command);
    }
}
