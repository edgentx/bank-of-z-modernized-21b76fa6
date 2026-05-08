package com.example.domain.vforce360.service;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.shared.ValidationPort;
import com.example.domain.validation.model.ValidationAggregate;
import org.springframework.stereotype.Service;

/**
 * Service for VForce360 specific defect validation logic.
 * Bridges the domain aggregates and the validation port.
 */
@Service
public class DefectValidationService implements ValidationPort {

    @Override
    public void validate(Object target) {
        if (target instanceof DefectAggregate) {
            DefectAggregate defect = (DefectAggregate) target;
            // Perform VForce360 specific validation logic
            if (defect.id() == null || defect.id().isBlank()) {
                throw new IllegalArgumentException("Defect ID cannot be blank");
            }
        } else if (target instanceof ValidationAggregate) {
            ValidationAggregate validation = (ValidationAggregate) target;
            // Perform specific validation on the validation aggregate
            if (validation.id() == null) {
                throw new IllegalArgumentException("Validation ID cannot be null");
            }
        } else {
            throw new IllegalArgumentException("Unknown target type for validation: " + target.getClass());
        }
    }
}
