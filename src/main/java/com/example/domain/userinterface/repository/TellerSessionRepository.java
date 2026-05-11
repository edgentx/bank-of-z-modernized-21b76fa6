package com.example.domain.userinterface.repository;

import com.example.domain.userinterface.model.TellerSessionAggregate;
import java.util.Optional;

public interface TellerSessionRepository {
  TellerSessionAggregate save(TellerSessionAggregate aggregate);
  Optional<TellerSessionAggregate> findById(String id);
}
