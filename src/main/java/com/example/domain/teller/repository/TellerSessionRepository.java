package com.example.domain.teller.repository;

import com.example.domain.shared.Aggregate;

public interface TellerSessionRepository {
    void save(Aggregate aggregate);
    Aggregate load(String id);
}
