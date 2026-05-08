package com.example.services;

import com.example.domain.shared.SlackMessageValidator;
import com.example.domain.shared.ValidationPort;
import org.springframework.stereotype.Service;

/**
 * Application service handling validation logic.
 * Delegates to the specific validator adapter implementing the ValidationPort contract.
 */
@Service
public class ValidationService implements ValidationPort {

    private final SlackMessageValidator validator;

    public ValidationService(SlackMessageValidator validator) {
        this.validator = validator;
    }

    @Override
    public void validateSlackBody(String content) throws SlackMessageValidator.SlackValidationException {
        validator.validateBodyContainsGitHubUrl(content);
    }
}
