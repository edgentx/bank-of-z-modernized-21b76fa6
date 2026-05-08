package com.example.adapters;

import com.example.domain.shared.ValidationException;
import com.example.domain.shared.ValidationPort;
import com.example.domain.vforce360.model.DefectReportedEvent;
import org.springframework.stereotype.Component;

@Component
public class DefectValidationAdapter implements ValidationPort {

    @Override
    public void validate(DefectReportedEvent event) throws ValidationException {
        if (event.metadata() == null || !event.metadata().containsKey("github_issue_url")) {
            throw new ValidationException("GitHub URL is missing from metadata");
        }
        String url = event.metadata().get("github_issue_url");
        if (url == null || url.isBlank() || !url.startsWith("http")) {
             throw new ValidationException("GitHub URL is invalid");
        }
    }
}
