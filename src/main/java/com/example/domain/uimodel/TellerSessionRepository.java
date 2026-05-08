package com.example.domain.uimodel;

import java.util.Optional;

public interface TellerSessionRepository {
  TellerSessionAggregate getOrCreate(String id);
  void save(TellerSessionAggregate aggregate);
}