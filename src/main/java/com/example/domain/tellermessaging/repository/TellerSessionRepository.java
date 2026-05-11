package com.example.domain.tellermessaging.repository;

import com.example.domain.tellermessaging.model.TellerSession;
import java.util.Optional;

public interface TellerSessionRepository {
    void save(TellerSession session);
    TellerSession load(String id);
    // Optional: FindById
}
