package com.example.domain.validation.service;

import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.repository.ValidationRepository;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {
    private final ValidationRepository repository;

    public ValidationService(ValidationRepository repository) {
        this.repository = repository;
    }

    /**
     * Validates that the text contains the required URL.
     * This is the business logic that was presumably broken or missing.
     */
    public boolean validateUrlPresence(String text, String requiredUrl) {
        if (text == null || requiredUrl == null) {
            return false;
        }
        return text.contains(requiredUrl);
    }
}
