package com.example.mocks;

import com.example.domain.shared.SlackMessageValidator;

public class FakeSlackMessageValidator implements SlackMessageValidator {
    @Override
    public boolean containsUrl(String messageBody, String targetUrl) {
        return messageBody != null && targetUrl != null && messageBody.contains(targetUrl);
    }
}
