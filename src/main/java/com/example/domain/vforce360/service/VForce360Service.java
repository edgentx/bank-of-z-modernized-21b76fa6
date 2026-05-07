package com.example.domain.vforce360.service;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.vforce360.model.VForce360Aggregate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service handling VForce360 domain logic.
 */
@Service
public class VForce360Service {

    private final VForce360Repository repository;

    public VForce360Service(VForce360Repository repository) {
        this.repository = repository;
    }

    public List<DomainEvent> process(String aggregateId, Command cmd) {
        VForce360Aggregate aggregate = repository.findById(aggregateId)
                .orElse(new VForce360Aggregate(aggregateId));
        List<DomainEvent> events = aggregate.execute(cmd);
        repository.save(aggregate);
        return events;
    }
}
