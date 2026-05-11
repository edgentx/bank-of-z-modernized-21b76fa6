package com.example.domain.screening.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.util.List;

/**
 * Aggregate Root for Screen Maps.
 * Manages the definition and validation logic for 3270 emulator screens.
 */
public class ScreenMap extends AggregateRoot {

    private final String screenId;
    // In a real implementation, this would contain BMS map definitions, field constraints, etc.

    public ScreenMap(String screenId) {
        this.screenId = screenId;
    }

    @Override
    public String id() {
        return screenId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof ValidateScreenInputCmd c) {
            return validateInput(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> validateInput(ValidateScreenInputCmd cmd) {
        // TDD: Logic to be implemented to make tests pass.
        // Currently empty to ensure RED phase.
        throw new UnsupportedOperationException("Logic not implemented yet");
    }
}
