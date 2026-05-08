package com.example.domain.teller.repository;

import com.example.domain.teller.model.TellerSession;

public interface TellerSessionRepository {
    TellerSession save(TellerSession aggregate);
    TellerSession findById(String id);
}
