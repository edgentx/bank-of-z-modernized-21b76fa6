package com.example.adapters;

import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.ports.SlackMessageValidator;
import org.springframework.stereotype.Component;

@Component
public class SlackMessageValidatorImpl implements SlackMessageValidator {

    @Override
    public boolean validate(ReportDefectCmd cmd, String generatedBody) {
        if (cmd.githubUrl() == null || cmd.githubUrl().isBlank()) {
            return false;
        }
        if (generatedBody == null) {
            return false;
        }
        return generatedBody.contains(cmd.githubUrl());
    }
}
