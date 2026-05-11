package com.example.domain.teller.repository;

import com.example.domain.teller.model.TellerSessionAggregate;
import java.util.Optional;

public interface TellerSessionRepository {
    void save(TellerSessionAggregate aggregate);
    TellerSessionAggregate findById(String id); // Returning Aggregate directly for simplicity in this context, or Optional
}