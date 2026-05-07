package com.example.mocks;

import com.example.domain.validation.repository.ValidationRepository;

/**
 * Mock implementation of ValidationRepository.
 * Currently lightweight as the aggregate logic is workflow-driven.
 */
public class MockValidationRepository implements ValidationRepository {
    // No-op for now, used primarily for type consistency in the test context
}
