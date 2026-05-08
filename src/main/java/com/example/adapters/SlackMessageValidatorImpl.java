package com.example.adapters;

import com.example.domain.ports.SlackMessageValidator;
import org.springframework.stereotype.Component;

@Component
public class SlackMessageValidatorImpl implements SlackMessageValidator {

    @Override
    public boolean validateBodyContainsUrl(String body, String expectedUrl) {
        if (body == null || expectedUrl == null) {
            return false;
        }
        return body.contains(expectedUrl);
    }
}
