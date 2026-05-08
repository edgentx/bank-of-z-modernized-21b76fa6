package com.example.adapters;

import com.example.domain.shared.SlackMessageValidator;

/**
 * Concrete implementation of the SlackMessageValidator interface.
 * Delegates to the core adapter logic but exists to satisfy class resolution
 * for the regression tests expecting this specific named class.
 */
public class SlackMessageValidatorImpl implements SlackMessageValidator {

    private final SlackMessageValidator delegate;

    public SlackMessageValidatorImpl() {
        // In a real Spring context, this would inject the actual SlackAdapter bean.
        // For the purpose of this compilation fix, we instantiate it directly.
        this.delegate = new SlackAdapter(null, "dummy-token"); // client is null, but isValid logic is pure
    }

    @Override
    public boolean isValid(String messageBody) {
        return delegate.isValid(messageBody);
    }
}
