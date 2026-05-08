package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerSessionAggregate;
import java.util.Optional;

public interface TellerSessionRepository {
    TellerSessionAggregate save(TellerSessionAggregate aggregate);
    Optional<TellerSessionAggregate> findById(String id);
    void deleteById(String id);
}
