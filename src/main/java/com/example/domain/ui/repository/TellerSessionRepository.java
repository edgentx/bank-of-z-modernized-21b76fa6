package com.example.domain.ui.repository;

import com.example.domain.ui.model.TellerSessionAggregate;
import java.util.Optional;

public interface TellerSessionRepository {
  void save(TellerSessionAggregate aggregate);
  Optional<TellerSessionAggregate> findById(String id);
}
