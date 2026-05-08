package com.example.domain.vforce360.service;

import com.example.domain.vforce360.model.DefectAggregate;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;

import java.util.List;

public class VForce360Workflow {
    private final DefectAggregate defectAggregate;

    public VForce360Workflow(DefectAggregate defectAggregate) {
        this.defectAggregate = defectAggregate;
    }

    public List<DomainEvent> handle(Command command) {
        return defectAggregate.execute(command);
    }
}
