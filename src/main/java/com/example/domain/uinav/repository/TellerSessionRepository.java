package com.example.domain.uinav.repository;

import com.example.domain.uinav.model.TellerSession;
import java.util.Optional;

public interface TellerSessionRepository {
    void save(TellerSession aggregate);
    Optional<TellerSession> findById(String id);
}
