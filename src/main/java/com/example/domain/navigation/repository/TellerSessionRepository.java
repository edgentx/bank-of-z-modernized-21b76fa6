package com.example.domain.navigation.repository;

import com.example.domain.navigation.model.TellerSession;
import java.util.Optional;

public interface TellerSessionRepository {
    TellerSession save(TellerSession aggregate);
    Optional<TellerSession> findById(String id);
    void deleteById(String id);
}
