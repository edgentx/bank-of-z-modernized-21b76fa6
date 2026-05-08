package com.example.services;

import com.example.domain.shared.SlackMessageValidator;
import com.example.domain.shared.ValidationPort;
import org.springframework.stereotype.Service;

/**
 * Application Service handling validation operations.
 * Acts as the primary entry point for validation use cases.
 */
@Service
public class ValidationService {

    private final ValidationPort validationPort;
    private final SlackMessageValidator slackValidator;

    public ValidationService(ValidationPort validationPort, SlackMessageValidator slackValidator) {
        this.validationPort = validationPort;
        this.slackValidator = slackValidator;
    }

    /**
     * Validates a target object using the registered ValidationPort.
     * @param target The object to validate.
     */
    public void validate(Object target) {
        validationPort.validate(target);
    }

    /**
     * Validates a string intended for a Slack message body.
     * S-FB-1: Ensures the body contains the required GitHub URL.
     * @param messageBody The message content.
     * @return true if valid, false otherwise.
     */
    public boolean validateSlackMessage(String messageBody) {
        return slackValidator.isValid(messageBody);
    }
}
