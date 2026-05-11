package com.example.domain.tellermaintenance.repository;

import com.example.domain.teller.model.TellerSessionAggregate;
import java.util.Optional;

public interface TellerSessionRepository {
    TellerSessionAggregate save(TellerSessionAggregate aggregate);
    Optional<TellerSessionAggregate> findById(String id);
    // Delete methods, findAll etc. if needed
}
